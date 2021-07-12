package de.otto.bitbucketwatcher.pullrequest

import de.otto.bitbucketwatcher.client.TeamsClient
import de.otto.bitbucketwatcher.client.TeamsMessageBuilder
import de.otto.bitbucketwatcher.model.PullRequest
import de.otto.bitbucketwatcher.model.ReviewerStatus
import de.otto.bitbucketwatcher.pullrequest.checker.ApprovalStatusChecker
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
