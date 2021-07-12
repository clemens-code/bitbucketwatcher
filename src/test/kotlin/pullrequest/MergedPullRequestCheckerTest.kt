package de.otto.bitbucketwatcher.pullrequest

import model.ReviewerStatus as ReviewerStatus1
import branches.BranchDeleter
import client.TeamsClient
import client.TeamsMessageBuilder
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import model.PullRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pullrequest.checker.MergedPullRequestChecker

class MergedPullRequestCheckerTest {

    private val client = mockk<TeamsClient>(relaxed = true)
    private val messageBuilder = mockk<TeamsMessageBuilder>(relaxed = true)
    private val deleter = mockk<BranchDeleter>(relaxed = true)

    private val mergedPullRequestChecker = MergedPullRequestChecker(client, messageBuilder, deleter)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun mergedPullRequest() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus1("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        mergedPullRequestChecker.publishMergedPullRequests(
            mutableMapOf(Pair(pullRequest.id, pullRequest)),
            getMergedPullRequests()
        )
        verify(atLeast = 1, atMost = 1) { deleter.deleteBranch(any()) }
        verify(atLeast = 1, atMost = 1) { client.postMessage(any()) }
    }

    @Test
    fun notMergedPullRequest() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus1("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"321\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        mergedPullRequestChecker.publishMergedPullRequests(
            mutableMapOf(Pair(pullRequest.id, pullRequest)),
            getMergedPullRequests()
        )
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { client.postMessage(any()) }
    }

    private fun getMergedPullRequests(): List<PullRequest> {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus1("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest1 =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        val pullRequest2 =
            PullRequest("\"124\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        return listOf(pullRequest1, pullRequest2)
    }
}
