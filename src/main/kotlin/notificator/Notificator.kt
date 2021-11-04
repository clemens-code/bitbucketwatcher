package io.github.clemenscode.bitbucketwatcher.notificator

import org.springframework.stereotype.Component
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.PullRequestMessage

interface Notificator{
    fun publishMessage(message: PullRequestMessage)
}

@Component
class PullRequestNotificator private constructor(val notificators: List<Notificator>){

    fun publish(message: PullRequestMessage) {
        for(notificator in notificators){
            notificator.publishMessage(message)
        }
    }
}