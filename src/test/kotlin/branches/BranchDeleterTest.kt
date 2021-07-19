package io.github.clemenscode.bitbucketwatcher.branches

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.model.BranchDeleteRequestModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class BranchDeleterTest {

    private val client = mockk<BitbucketClient>()
    private val constants = mockk<BitbucketConstants>()

    private val deleter = BranchDeleter(client, constants)

    @Test
    fun deleteBranch() {
        every { client.deleteBranchById(any(), any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        coEvery { constants.projectKey }.returns("test")
        coEvery { constants.repoSlug }.returns("test")
        val id = "\"123\""
        val cutId = "123"
        deleter.deleteBranch(id)
        verify(atLeast = 1, atMost = 1) { client.deleteBranchById(any(), any(), BranchDeleteRequestModel(cutId)) }
    }

    @Test
    fun dontDeleteMaster() {
        every { client.deleteBranchById(any(), any(), any()) }.returns(ObjectNode(JsonNodeFactory(true)))
        val id = "\"refs/heads/master\""
        deleter.deleteBranch(id)
        verify(atLeast = 0, atMost = 0) { client.deleteBranchById(any(), any(), any()) }
    }
}
