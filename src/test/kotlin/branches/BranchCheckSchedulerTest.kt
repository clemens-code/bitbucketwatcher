package io.github.clemenscode.bitbucketwatcher.branches

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.client.builder.PullRequestMessages
import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.model.Branch
import io.github.clemenscode.bitbucketwatcher.notificator.PullRequestNotificator
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BranchCheckSchedulerTest {

    private val branchBuilder = mockk<BranchBuilder>()
    private val bitbucketClient = mockk<BitbucketClient>()
    private val deleter = mockk<BranchDeleter>()
    private val pullRequestMessages = mockk<PullRequestMessages>(relaxed = true)
    private val constants = mockk<BitbucketConstants>(relaxed = true)
    private val notificator = mockk<PullRequestNotificator>(relaxed = true)

    private val branchCheckScheduler =
        BranchCheckScheduler(
            branchBuilder,
            bitbucketClient,
            deleter,
            pullRequestMessages,
            constants,
            notificator
        )

    @Test
    fun checkForFinishedBranchesTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(listOf(Branch("123", 1617228000000)))
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 1, atMost = 1) { notificator.publish(any()) }
    }

    @Test
    fun checkNoOldBrancheTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(
            listOf(
                Branch(
                    "123",
                    System.currentTimeMillis()
                )
            )
        )
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { notificator.publish(any()) }
    }

    @Test
    fun noMessageForMasterTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(listOf(Branch("\"refs/heads/master\"", 1617228000000)))
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { notificator.publish(any()) }
    }

    @Test
    fun openPrForBranchTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(
            listOf(
                Branch(
                    "\"refs/heads/feature/test\"",
                    System.currentTimeMillis(),
                    "\"OPEN\""
                )
            )
        )
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { notificator.publish(any()) }
    }

    @Test
    fun mergedPrForBranchTest() {
        val id = "\"refs/heads/feature/test\""
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(
            listOf(
                Branch(
                    id,
                    System.currentTimeMillis(),
                    "\"MERGED\""
                )
            )
        )
        coEvery { deleter.deleteBranch(any()) }.returns(Unit)
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 1, atMost = 1) { deleter.deleteBranch(id) }
        verify(atLeast = 0, atMost = 0) { notificator.publish(any()) }
    }
}
