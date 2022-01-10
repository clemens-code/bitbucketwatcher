package io.github.clemenscode.bitbucketwatcher.notificator

import org.springframework.stereotype.Component

internal interface Notificator{
    fun publishMessage(message: PullRequestMessage)
}

data class PullRequestMessage(val title: String, val message: String)

@Component
internal class PullRequestNotificator(val notificators: List<Notificator>){
    fun publish(message: PullRequestMessage) {
        for(notificator in notificators){
            notificator.publishMessage(message)
        }
    }
}
