package io.github.clemenscode.bitbucketwatcher.client.builder

import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.model.Branch
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.model.TeamsMessage
import org.springframework.stereotype.Component

private const val BRANCH_TEAMS_TEXT = "Branch has not been updated since at least a week!"

@Component
internal class TeamsMessageBuilder(
    private val bitbucketConstants: BitbucketConstants
) {

    fun newPRMessage(pullRequest: PullRequest) = TeamsMessage(
        title = getTitleForNewPR(pullRequest.title, pullRequest.authorName),
        text = getDefaultPRMessage(pullRequest.id)
    )

    private fun getDefaultPRMessage(id: String) =
        "<a href=\"${bitbucketLinkUrl()}/$id/overview\"> Pull Request </a>"

    private fun getTitleForNewPR(title: String, name: String) = "New PR $title by $name"

    fun statusChangeMessage(pullRequest: PullRequest, reviewer: String, status: String) = TeamsMessage(
        title = getTitleForStatusUpdate(pullRequest.title, reviewer, status),
        text = getDefaultPRMessage(pullRequest.id)
    )

    private fun getTitleForStatusUpdate(title: String, reviewer: String, status: String) =
        "$reviewer changed status to $status at $title"

    fun mergedPRMessage(pullRequest: PullRequest) = TeamsMessage(
        title = getMergedTitle(pullRequest.title),
        text = getDefaultPRMessage(pullRequest.id)
    )

    private fun getMergedTitle(title: String) = "PR $title has been merged."

    fun oldBranchMessage(branch: Branch) = TeamsMessage(
        title = getTitleForBranch(branch.id),
        text = BRANCH_TEAMS_TEXT
    )

    private fun getTitleForBranch(id: String) = "Is the $id still needed?"

    private fun bitbucketLinkUrl() =
        bitbucketConstants.baseUrl + "projects/" +
            bitbucketConstants.projectKey + "/repos/" +
            bitbucketConstants.repoSlug + "/"
}
