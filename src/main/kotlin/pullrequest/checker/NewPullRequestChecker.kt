package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.TeamsMessageBuilder
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
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
