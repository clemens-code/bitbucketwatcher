package io.github.clemenscode.bitbucketwatcher.notificator

import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.model.TeamsMessage
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.PullRequestMessage

internal class TeamNotificator(private val teamsClient: TeamsClient): Notificator{
    override fun publishMessage(message: PullRequestMessage) {
        teamsClient.postMessage(TeamsMessage(title = message.title, text = message.message))
    }
}