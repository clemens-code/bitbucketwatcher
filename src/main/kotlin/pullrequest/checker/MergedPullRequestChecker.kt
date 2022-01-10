package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import feign.FeignException
import io.github.clemenscode.bitbucketwatcher.branches.BranchDeleter
import io.github.clemenscode.bitbucketwatcher.client.builder.PullRequestMessages
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import org.springframework.stereotype.Component

@Component
internal class MergedPullRequestChecker(
    private val pullRequestMessages: PullRequestMessages,
    private val deleter: BranchDeleter,
    private val notificator: PullRequestNotificator
) {
    private val logger = getLogger(MergedPullRequestChecker::class.java)

    fun publishMergedPullRequests(
        publishedPRs: MutableMap<String, PullRequest>,
        mergedPullRequests: List<PullRequest>
    ): MutableMap<String, PullRequest> {
        mergedPullRequests
            .forEach {
                if (publishedPRs[it.id] != null) {
                    logger.info("Merged ${it.title} sending it to teams")
                    try {
                        deleter.deleteBranch(it.branchId)
                    } catch (e: FeignException) {
                        logger.warn("Could not delete Branch of merged PR ${it.title}", e)
                    }
                    notificator.publish(pullRequestMessages.mergedPRMessage(it))
                    publishedPRs.remove(it.id)
                }
            }
        return publishedPRs
    }
}
