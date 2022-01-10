package io.github.clemenscode.bitbucketwatcher.client.builder

import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.model.Branch
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestMessage
import org.springframework.stereotype.Component

private const val BRANCH_TEAMS_TEXT = "Branch has not been updated since at least a week!"

@Component
internal class PullRequestMessages(
        private val bitbucketConstants: BitbucketConstants
) {

    fun newPRMessage(pullRequest: PullRequest) = PullRequestMessage(
            title = getTitleForNewPR(pullRequest.title, pullRequest.authorName),
            message = getDefaultPRMessage(pullRequest.id)
    )

    fun statusChangeMessage(pullRequest: PullRequest, reviewer: String, status: String) = PullRequestMessage(
            title = getTitleForStatusUpdate(pullRequest.title, reviewer, status),
            message = getDefaultPRMessage(pullRequest.id)
    )

    fun mergedPRMessage(pullRequest: PullRequest) = PullRequestMessage(
            title = getMergedTitle(pullRequest.title),
            message = getDefaultPRMessage(pullRequest.id)
    )

    fun oldBranchMessage(branch: Branch) = PullRequestMessage(
            title = getTitleForBranch(branch.id),
            message = BRANCH_TEAMS_TEXT
    )

    private fun getDefaultPRMessage(id: String) =
            "<a href=\"${bitbucketLinkUrl()}/$id/overview\"> Pull Request </a>"

    private fun getTitleForNewPR(title: String, name: String) = "New PR $title by $name"

    private fun getTitleForStatusUpdate(title: String, reviewer: String, status: String) =
            "$reviewer changed status to $status at $title"

    private fun getMergedTitle(title: String) = "PR $title has been merged."

    private fun getTitleForBranch(id: String) = "Is the $id still needed?"

    private fun bitbucketLinkUrl() =
            bitbucketConstants.baseUrl + "projects/" +
                    bitbucketConstants.projectKey + "/repos/" +
                    bitbucketConstants.repoSlug + "/"
}
