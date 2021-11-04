package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.TelegramClient
import io.github.clemenscode.bitbucketwatcher.client.builder.TeamsMessageBuilder
import io.github.clemenscode.bitbucketwatcher.client.builder.TelegramMessageBuilder
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.model.ReviewerStatus
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

internal class BitbucketPullReqestChecker() : Checker {

    private val publishedPRs = mutableListOf<String>()
    private val mergedPRs = mutableListOf<String>()
    private val latestApprovalStatus = mutableMapOf<String, String>()

    override fun check(pullRequest: PullRequest): List<CheckerResult> {
        return if (publishedPRs.contains(pullRequest.id)) {
            changedReviewStatus(pullRequest).map{
                CheckerResult(PullRequestMessage("New PR!", "New PR ${pullRequest.title} from ${pullRequest.authorName}"))
            }
        } else {
            publishedPRs.add(pullRequest.id)
            setLatestReviewerStatus(pullRequest)
            listOf(CheckerResult(PullRequestMessage("New PR!", "New PR ${pullRequest.title} from ${pullRequest.authorName}")))
        }
       //TODO überlegen wie man mit germerden PR's umgeht, eventuell muss immer die Id von jedem gemergeden PR mitgegeben werden
    }

    private fun setLatestReviewerStatus(pullRequest: PullRequest) {
        pullRequest.statusByReviewers.forEach {
            latestApprovalStatus[pullRequest.id + it.reviewer] = it.status
        }
    }

    private fun changedReviewStatus(pullRequest: PullRequest): List<ReviewerStatus> {
        val changedStatus = mutableListOf<ReviewerStatus>()
        pullRequest.statusByReviewers.map {
            if(latestApprovalStatus[pullRequest.id + it.reviewer] == it.status){
                changedStatus.add(it)
            }
        }
        return changedStatus
    }
}
