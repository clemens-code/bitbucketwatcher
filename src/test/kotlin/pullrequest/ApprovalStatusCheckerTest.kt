package de.otto.bitbucketwatcher.pullrequest

import client.TeamsClient
import client.TeamsMessageBuilder
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import model.PullRequest
import model.ReviewerStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pullrequest.checker.ApprovalStatusChecker

class ApprovalStatusCheckerTest {

    private val client = mockk<TeamsClient>(relaxed = true)
    private val messageBuilder = mockk<TeamsMessageBuilder>(relaxed = true)

    private val approvalStatusChecker = ApprovalStatusChecker(client, messageBuilder)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun publishNewApproval() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        approvalStatusChecker.publishNewApprovalStatus(pullRequest)

        verify(atLeast = 1, atMost = 1) { client.postMessage(any()) }
    }

    @Test
    fun checkTwoApprovals() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        approvalStatusChecker.publishNewApprovalStatus(pullRequest)
        approvalStatusChecker.publishNewApprovalStatus(pullRequest)
        verify(atLeast = 1, atMost = 1) { client.postMessage(any()) }
    }

    @Test
    fun checkChangedApprovals() {
        val updateTime = 1213456L
        val reviewerChanged = ReviewerStatus("\"testReviewer\"", "\"APPROVED\"")
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"NEEDS_WORK\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        val pullRequestChanged =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewerChanged))
        approvalStatusChecker.publishNewApprovalStatus(pullRequest)
        approvalStatusChecker.publishNewApprovalStatus(pullRequestChanged)
        verify(atLeast = 2, atMost = 2) { client.postMessage(any()) }
    }

    @Test
    fun checkUNNAPROVED() {
        val updateTime = 1213456L
        val reviewer = ReviewerStatus("\"testReviewer\"", "\"UNAPPROVED\"")
        val pullRequest =
            PullRequest("\"123\"", "\"test-PR\"", "branchId", "\"testAuthor\"", updateTime, listOf(reviewer))
        approvalStatusChecker.publishNewApprovalStatus(pullRequest)
        verify(atLeast = 0, atMost = 0) { client.postMessage(any()) }
    }
}
