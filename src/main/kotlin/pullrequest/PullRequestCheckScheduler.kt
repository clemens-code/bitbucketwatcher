package io.github.clemenscode.bitbucketwatcher.pullrequest

import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.ApprovalStatusChecker
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.MergedPullRequestChecker
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.NewPullRequestChecker
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private const val SCHEDULE_DELAY = 60000L

@Component
internal class PullRequestCheckScheduler(
    private val bitbucketClient: BitbucketClient,
    private val pullRequestBuilder: PullRequestBuilder,
    private val approvalStatusChecker: ApprovalStatusChecker,
    private val newPullRequestChecker: NewPullRequestChecker,
    private val mergedPullRequestChecker: MergedPullRequestChecker,
    private val bitbucketConstants: BitbucketConstants
) {

    private val logger = getLogger(PullRequestCheckScheduler::class.java)
    private var alreadyPublishedPRs = mutableMapOf<String, PullRequest>()

    @EventListener(value = [ApplicationReadyEvent::class])
    fun onStartup() {
        checkForPullRequestsToPublish()
    }

    @Scheduled(fixedDelay = SCHEDULE_DELAY)
    fun checkForPullRequestsToPublish() {
        logger.info("Start checking for new Pull Requests!")
        val openPRs = pullRequestBuilder.requestedPullRequests(requestNewestPRs())
        val mergedPRs = pullRequestBuilder.requestedPullRequests(requestMergedPRs())
        logger.info("Found ${openPRs.size} open Pull Requests.")
        alreadyPublishedPRs = mergedPullRequestChecker.publishMergedPullRequests(alreadyPublishedPRs, mergedPRs)
        openPRs
            .filter { isAlreadyPublished(it) }
            .forEach {
                approvalStatusChecker.publishNewApprovalStatus(it)
            }
        openPRs
            .filter { !isAlreadyPublished(it) }
            .forEach {
                newPullRequestChecker.publishNewPullRequests(it)
                alreadyPublishedPRs[it.id] = it
            }
    }

    private fun isAlreadyPublished(pullRequest: PullRequest) =
        alreadyPublishedPRs[pullRequest.id] != null

    private fun requestNewestPRs() =
        bitbucketClient.getOpenPullRequests(bitbucketConstants.projectKey, bitbucketConstants.repoSlug)

    private fun requestMergedPRs() =
        bitbucketClient.getMergedPullRequests(bitbucketConstants.projectKey, bitbucketConstants.repoSlug)
}
