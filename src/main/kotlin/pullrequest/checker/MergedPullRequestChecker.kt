package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.branches.BranchDeleter
import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.TeamsMessageBuilder
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import org.springframework.stereotype.Component

@Component
internal class MergedPullRequestChecker(
    private val teamsClient: TeamsClient,
    private val teamsMessageBuilder: TeamsMessageBuilder,
    private val deleter: BranchDeleter
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
                    teamsClient.postMessage(teamsMessageBuilder.mergedPRMessage(it))
                    publishedPRs.remove(it.id)
                    deleter.deleteBranch(it.branchId)
                }
            }
        return publishedPRs
    }
}
