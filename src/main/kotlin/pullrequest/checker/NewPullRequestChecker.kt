package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.client.builder.PullRequestMessages
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import org.springframework.stereotype.Component

@Component
internal class NewPullRequestChecker(
        private val pullRequestMessages: PullRequestMessages,
        private val notificator: PullRequestNotificator
) {

    private val logger = getLogger(NewPullRequestChecker::class.java)

    fun publishNewPullRequests(pullRequest: PullRequest) {
        notificator.publish(pullRequestMessages.newPRMessage(pullRequest))
        logger.info("Just send a message to Teams and Telegram.")
    }
}
