package io.github.clemenscode.bitbucketwatcher.branches

import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.builder.TeamsMessageBuilder
import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.Branch
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal const val MASTER_ID = "\"refs/heads/master\""
private const val MERGED = "\"MERGED\""

@Component
internal class BranchCheckScheduler(
    private val branchBuilder: BranchBuilder,
    private val bitbucketClient: BitbucketClient,
    private val teamsClient: TeamsClient,
    private val deleter: BranchDeleter,
    private val teamsMessageBuilder: TeamsMessageBuilder,
    private val bitbucketConstants: BitbucketConstants
) {
    private val logger = getLogger(BranchCheckScheduler::class.java)

    private val publishedBranches = mutableListOf<Branch>()

    @EventListener(value = [ApplicationReadyEvent::class])
    fun onStartUp() {
        checkForFinishedBranches()
    }

    @Scheduled(cron = "\${bitbucket.branch.cron-check}")
    fun checkForFinishedBranches() {
        logger.info("Start check for old branches.")
        val branches = branchBuilder.buildBranches(
            bitbucketClient.getAllBranches(
                bitbucketConstants.projectKey,
                bitbucketConstants.repoSlug
            )
        )
        branches
            .filter { it.id != MASTER_ID }
            .let {
                deleteMergedBranches(it)
                notifyOldBranch(it)
            }
    }

    private fun deleteMergedBranches(branches: List<Branch>) {
        branches
            .filter { it.status == MERGED }
            .forEach { deleter.deleteBranch(it.id) }
    }

    private fun notifyOldBranch(branches: List<Branch>) {
        branches
            .filter { isOlderThanAWeek(it.lastCommit) }
            .filter { !publishedBranches.contains(it) }
            .forEach {
                publishedBranches.add(it)
                teamsClient.postMessage(teamsMessageBuilder.oldBranchMessage(it))
            }
    }

    /**
     * Löscht einmal täglich, um 7 Uhr, alle veröffentlichten Branches
     */
    @Scheduled(cron = "\${bitbucket.branch.cron-clear}")
    private fun clearPublishedBranches() = publishedBranches.clear()

    private fun convertToLocalDateTime(millis: Long) =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()

    private fun isOlderThanAWeek(lastCommit: Long) =
        convertToLocalDateTime(lastCommit).plusWeeks(1L).isBefore(LocalDateTime.now())
}
