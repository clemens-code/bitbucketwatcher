package de.otto.bitbucketwatcher.branches

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.otto.bitbucketwatcher.client.BitbucketClient
import de.otto.bitbucketwatcher.client.TeamsClient
import de.otto.bitbucketwatcher.client.TeamsMessageBuilder
import de.otto.bitbucketwatcher.common.BitbucketConstants
import de.otto.bitbucketwatcher.model.Branch
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BranchCheckSchedulerTest {

    private val branchBuilder = mockk<BranchBuilder>()
    private val bitbucketClient = mockk<BitbucketClient>()
    private val teamsClient = mockk<TeamsClient>()
    private val deleter = mockk<BranchDeleter>()
    private val teamsMessageBuilder = mockk<TeamsMessageBuilder>(relaxed = true)
    private val constants = mockk<BitbucketConstants>(relaxed = true)

    private val branchCheckScheduler =
        BranchCheckScheduler(
            branchBuilder,
            bitbucketClient,
            teamsClient,
            deleter,
            teamsMessageBuilder,
            constants
        )

    @Test
    fun checkForFinishedBranchesTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(listOf(Branch("123", 1617228000000)))
        coEvery { teamsClient.postMessage(any()) }.returns(Unit)
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 1, atMost = 1) { teamsClient.postMessage(any()) }
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
        coEvery { teamsClient.postMessage(any()) }.returns(Unit)
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { teamsClient.postMessage(any()) }
    }

    @Test
    fun noMessageForMasterTest() {
        coEvery { bitbucketClient.getAllBranches(any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { branchBuilder.buildBranches(any()) }.returns(listOf(Branch("\"refs/heads/master\"", 1617228000000)))
        coEvery { teamsClient.postMessage(any()) }
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { teamsClient.postMessage(any()) }
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
        coEvery { teamsClient.postMessage(any()) }
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 0, atMost = 0) { deleter.deleteBranch(any()) }
        verify(atLeast = 0, atMost = 0) { teamsClient.postMessage(any()) }
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
        coEvery { teamsClient.postMessage(any()) }
        coEvery { deleter.deleteBranch(any()) }.returns(Unit)
        branchCheckScheduler.checkForFinishedBranches()
        verify(atLeast = 1, atMost = 1) { deleter.deleteBranch(id) }
        verify(atLeast = 0, atMost = 0) { teamsClient.postMessage(any()) }
    }
}
