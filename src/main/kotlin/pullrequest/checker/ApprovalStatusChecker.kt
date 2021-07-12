package pullrequest.checker

import client.TeamsClient
import client.TeamsMessageBuilder
import model.PullRequest
import model.ReviewerStatus
import org.springframework.stereotype.Component

private const val UNAPPROVED = "\"UNAPPROVED\""

@Component
internal class ApprovalStatusChecker(
    private val teamsClient: TeamsClient,
    private val teamsMessageBuilder: TeamsMessageBuilder
) {

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
                teamsClient.postMessage(teamsMessageBuilder.statusChangeMessage(pullRequest, it.reviewer, it.status))
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
