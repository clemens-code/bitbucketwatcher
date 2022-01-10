package de.otto.bitbucketwatcher.pullrequest

import io.github.clemenscode.bitbucketwatcher.branches.BranchDeleter
import io.github.clemenscode.bitbucketwatcher.client.builder.PullRequestMessages
import io.github.clemenscode.bitbucketwatcher.model.PullRequest
import io.github.clemenscode.bitbucketwatcher.model.ReviewerStatus
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.MergedPullRequestChecker
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MergedPullRequestCheckerTest {

    private val messageBuilder = mockk<PullRequestMessages>(relaxed = true)
    private val deleter = mockk<BranchDeleter>(relaxed = true)
    private val notificator = mockk<PullRequestNotificator>(relaxed = true)

    private val mergedPullRequestChecker =
        MergedPullRequestChecker(messageBuilder, deleter, notificator)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun mergedPullRequest() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        mergedPullRequestChecker.publishMergedPullRequests(
            mutableMapOf(Pair(pullRequest.id, pullRequest)),
            getMergedPullRequests()
        )
        verify(atLeast = 1, atMost = 1) { deleter.deleteBranch(any()) }
        verify(atLeast = 1, atMost = 1) { notificator.publish(any()) }
    }

    @Test
    fun notMergedPullRequest() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"321\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        mergedPullRequestChecker.publishMergedPullRequests(
            mutableMapOf(Pair(pullRequest.id, pullRequest)),
            getMergedPullRequests()
        )
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { notificator.publish(any()) }
    }

    private fun getMergedPullRequests(): List<PullRequest> {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest1 =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        val pullRequest2 =
            PullRequest("\"124\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        return listOf(pullRequest1, pullRequest2)
    }
}
