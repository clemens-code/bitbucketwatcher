package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.TelegramClient
import io.github.clemenscode.bitbucketwatcher.client.builder.TeamsMessageBuilder
import io.github.clemenscode.bitbucketwatcher.client.builder.TelegramMessageBuilder
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import org.springframework.stereotype.Component

@Component
internal class NewPullRequestChecker(
    private val teamsClient: TeamsClient,
    private val teamsMessageBuilder: TeamsMessageBuilder,
    private val telegramClient: TelegramClient,
    private val telegramMessageBuilder: TelegramMessageBuilder
) {

    private val logger = getLogger(NewPullRequestChecker::class.java)

    /**
     * publishes the unknown PulLRequests
     */
    fun publishNewPullRequests(pullRequest: PullRequest) {
        teamsClient.postMessage(teamsMessageBuilder.newPRMessage(pullRequest))
        telegramClient.postMessage(
            telegramMessageBuilder.buildTelegramMessage(
                "New PR ${pullRequest.title} from ${pullRequest.authorName}"
            )
        )
        logger.info("Just send a message to Teams and Telegram.")
    }
}
