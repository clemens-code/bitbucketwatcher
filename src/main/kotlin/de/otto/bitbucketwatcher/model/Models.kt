package de.otto.bitbucketwatcher.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PullRequest(
    val id: String,
    val title: String,
    val branchId: String,
    val authorName: String,
    val updatedDate: Long,
    val statusByReviewers: List<ReviewerStatus>
)

data class ReviewerStatus(
    val reviewer: String,
    val status: String
)

data class Branch(
    @JsonProperty("id")
    val id: String = "0",
    @JsonProperty("authorTimestamp")
    val lastCommit: Long = 123L,
    @JsonProperty("state")
    val status: String? = null
)

data class BranchDeleteRequestModel(
    val name: String,
    val dryRun: Boolean = false
)
