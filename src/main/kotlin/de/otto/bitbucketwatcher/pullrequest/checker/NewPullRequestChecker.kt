package de.otto.bitbucketwatcher.pullrequest.checker

import de.otto.bitbucketwatcher.client.TeamsClient
import de.otto.bitbucketwatcher.client.TeamsMessageBuilder
import de.otto.bitbucketwatcher.model.PullRequest
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
