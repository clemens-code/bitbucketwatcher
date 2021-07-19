package io.github.clemenscode.bitbucketwatcher.branches

import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.clemenscode.bitbucketwatcher.model.Branch
import org.springframework.stereotype.Component

private const val LAST_COMMIT_NODE = "com.atlassian.bitbucket.server.bitbucket-branch:latest-commit-metadata"
private const val STATUS_NODE = "com.atlassian.bitbucket.server.bitbucket-ref-metadata:outgoing-pull-request-metadata"
private const val METADATA = "metadata"

@Component
class BranchBuilder {
    /**
     * extracts a Branch form the Bitbucket response
     */
    fun buildBranches(objectNode: ObjectNode): List<Branch> {
        val branches = mutableListOf<Branch>()
        val values = objectNode.get("values")
        values.forEach {
            branches.add(
                Branch(
                    it.get("id").toPrettyString(),
                    it.get(METADATA).get(LAST_COMMIT_NODE).get("authorTimestamp").asLong(),
                    it.get(METADATA)?.get(STATUS_NODE)?.get("pullRequest")?.get("state")?.toPrettyString()
                )
            )
        }
        return branches
    }
}
