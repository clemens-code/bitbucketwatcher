package io.github.clemenscode.bitbucketwatcher.notificator

import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.model.TeamsMessage
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@ConditionalOnBean(TeamsClient::class)
@Component
internal class TeamsNotificator(private val teamsClient: TeamsClient) : Notificator {
    override fun publishMessage(message: PullRequestMessage) {
        teamsClient.postMessage(TeamsMessage(title = message.title, text = message.message))
    }
}
