package branches

import client.BitbucketClient
import common.BitbucketConstants
import logger.getLogger
import model.BranchDeleteRequestModel
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
