package io.github.clemenscode.bitbucketwatcher.branches

import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.common.BitbucketConstants
import io.github.clemenscode.bitbucketwatcher.logger.getLogger
import io.github.clemenscode.bitbucketwatcher.model.BranchDeleteRequestModel
import org.springframework.stereotype.Component

@Component
internal class BranchDeleter(
    private val client: BitbucketClient,
    private val constants: BitbucketConstants
) {

    private val logger = getLogger(BranchDeleter::class.java)

    /**
     * If the deletion isn't successful it is expected that Bitbucket automatically has deleted the merged Branch.
     */
    fun deleteBranch(id: String) {
        val cutId = removeQuotes(id)
        logger.info("Sending delete request for $cutId")
        if (id != MASTER_ID) {
            client.deleteBranchById(constants.projectKey, constants.repoSlug, BranchDeleteRequestModel(cutId))?.let {
                if (it.isEmpty) {
                    logger.warn("Failed to delete $cutId. Response from Bitbucket: $it")
                } else {
                    logger.info("Successfully $cutId deleted")
                }
            }
        }
    }

    private fun removeQuotes(id: String) = id.replace("\"", "")
}
