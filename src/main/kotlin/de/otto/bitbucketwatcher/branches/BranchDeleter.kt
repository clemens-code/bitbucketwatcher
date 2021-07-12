package de.otto.bitbucketwatcher.branches

import de.otto.bitbucketwatcher.client.BitbucketClient
import de.otto.bitbucketwatcher.common.BitbucketConstants
import de.otto.bitbucketwatcher.logger.getLogger
import de.otto.bitbucketwatcher.model.BranchDeleteRequestModel
import org.springframework.stereotype.Component

@Component
internal class BranchDeleter(
    private val client: BitbucketClient,
    private val constants: BitbucketConstants
) {

    private val logger = getLogger(BranchDeleter::class.java)

    fun deleteBranch(id: String) {
        val cutId = removeQuotes(id)
        logger.info("Sending delete request for $cutId")
        if (id != MASTER_ID) {
            client.deleteBranchById(constants.projectKey, constants.repoSlug, BranchDeleteRequestModel(cutId)).let {
                if (it.isEmpty) {
                    logger.error("Failed to delete $cutId. Response from Bitbucket: $it")
                } else {
                    logger.info("Successfully $cutId deleted")
                }
            }
        }
    }

    private fun removeQuotes(id: String) = id.replace("\"", "")
}
