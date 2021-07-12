package pullrequest.checker

import client.TeamsClient
import client.TeamsMessageBuilder
import model.PullRequest
import org.springframework.stereotype.Component

@Component
internal class NewPullRequestChecker(
    private val teamsClient: TeamsClient,
    private val teamsMessageBuilder: TeamsMessageBuilder
) {

    /**
     * publishes the unknown PulLRequests
     */
    fun publishNewPullRequests(pullRequest: PullRequest) {
        teamsClient.postMessage(teamsMessageBuilder.newPRMessage(pullRequest))
    }
}
