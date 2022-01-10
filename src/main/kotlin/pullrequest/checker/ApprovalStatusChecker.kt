package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.client.builder.PullRequestMessages
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.model.ReviewerStatus
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import org.springframework.stereotype.Component

private const val UNAPPROVED = "\"UNAPPROVED\""

@Component
internal class ApprovalStatusChecker(
        private val pullRequestMessages: PullRequestMessages,
        private val notificator: PullRequestNotificator
) {

    private val logger = getLogger(ApprovalStatusChecker::class.java)
    private val latestApprovalStatus = mutableMapOf<String, String>()

    /**
     * checks if any ApprovalStaus has changed since last check
     * only publishes if the Status has changed and if the status ain't UNAPPROVED
     * in the end; overwrites the known ApprovalStatus
     */
    fun publishNewApprovalStatus(pullRequest: PullRequest) {
        pullRequest.statusByReviewers
                .filter { it.status != UNAPPROVED }
                .filter { !isAlreadyPublishedReviewStatus(pullRequest.id, it) }
                .forEach {
                    notificator.publish(pullRequestMessages.statusChangeMessage(pullRequest, it.reviewer, it.status))
                    logger.info("Send new Approval status for ${pullRequest.title}")
                }
        writeApprovalStatus(pullRequest)
    }

    private fun isAlreadyPublishedReviewStatus(pullRequestId: String, reviewStatus: ReviewerStatus) =
            latestApprovalStatus[pullRequestId + reviewStatus.reviewer] == reviewStatus.status

    private fun writeApprovalStatus(pullRequest: PullRequest) {
        pullRequest.statusByReviewers.forEach { reviewerStatus ->
            latestApprovalStatus[pullRequest.id + reviewerStatus.reviewer] = reviewerStatus.status
        }
    }
}
