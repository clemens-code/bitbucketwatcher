package io.github.clemenscode.bitbucketwatcher.pullrequest.checker

import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import org.springframework.stereotype.Component

internal interface Checker {
    fun check(pullRequest: PullRequest): List<CheckerResult>
}

internal data class CheckerResult(val message: PullRequestMessage? = null)
data class PullRequestMessage(val title: String, val message: String)

@Component
internal class PullRequestChecker(val checkers: List<Checker>, private val notificator: PullRequestNotificator) {

    fun checkPullRequests(pullRequest: PullRequest) {
        for (checker in checkers) {
            checker.check(pullRequest).forEach {
                if (it.isUnPublished) {
                    notificator.publish(it.message ?: error("Not able to publish notification without message"))
                }
            }
        }
    }
}
