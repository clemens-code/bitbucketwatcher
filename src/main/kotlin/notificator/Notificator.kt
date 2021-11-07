package io.github.clemenscode.bitbucketwatcher.notificator

import org.springframework.stereotype.Component

interface Notificator{
    fun publishMessage(message: PullRequestMessage)
}

data class PullRequestMessage(val title: String, val message: String)

@Component
class PullRequestNotificator private constructor(val notificators: List<Notificator>){
    fun publish(message: PullRequestMessage) {
        for(notificator in notificators){
            notificator.publishMessage(message)
        }
    }
}