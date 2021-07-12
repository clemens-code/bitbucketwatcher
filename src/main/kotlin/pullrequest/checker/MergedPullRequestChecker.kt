package pullrequest.checker

import branches.BranchDeleter
import client.TeamsClient
import client.TeamsMessageBuilder
import model.PullRequest
import org.springframework.stereotype.Component

@Component
internal class MergedPullRequestChecker(
    private val teamsClient: TeamsClient,
    private val teamsMessageBuilder: TeamsMessageBuilder,
    private val deleter: BranchDeleter
) {

    fun publishMergedPullRequests(
        publishedPRs: MutableMap<String, PullRequest>,
        mergedPullRequests: List<PullRequest>
    ): MutableMap<String, PullRequest> {
        mergedPullRequests
            .forEach {
                if (publishedPRs[it.id] != null) {
                    teamsClient.postMessage(teamsMessageBuilder.mergedPRMessage(it))
                    publishedPRs.remove(it.id)
                    deleter.deleteBranch(it.branchId)
                }
            }
        return publishedPRs
    }
}
