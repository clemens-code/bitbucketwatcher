package de.otto.bitbucketwatcher.pullrequest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.otto.bitbucketwatcher.model.PullRequest
import de.otto.bitbucketwatcher.model.ReviewerStatus
import org.springframework.stereotype.Component

@Component
class PullRequestBuilder {

    /**
     * extracts PullRequests from a given ResponsEntity
     * returns a List<PullRequests>
     */
    fun requestedPullRequests(objectNode: ObjectNode): MutableList<PullRequest> {
        val requestedPRs = mutableListOf<PullRequest>()
        val values = objectNode.get("values")
        values.forEach {
            requestedPRs.add(
                PullRequest(
                    it.get("id").toString(), it.get("title").toString(),
                    it.get("fromRef").get("id").toString(),
                    it.get("author").get("user").get("name").toString(),
                    it.get("updatedDate").asLong(), getReviewers(it.get("reviewers"))
                )
            )
        }
        return requestedPRs
    }

    private fun getReviewers(reviewers: JsonNode): List<ReviewerStatus> {
        return reviewers.map {
            ReviewerStatus(
                it.get("user").get("name").toString(),
                it.get("status").toString()
            )
        }
    }
}
