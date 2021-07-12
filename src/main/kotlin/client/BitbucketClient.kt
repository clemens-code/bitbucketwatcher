package client

import com.fasterxml.jackson.databind.node.ObjectNode
import feign.Headers
import feign.Param
import feign.RequestLine
import model.BranchDeleteRequestModel

private const val URL_PATH = "rest/api/1.0/projects/{project}/repos/{reposlug}"
private const val BRANCH_DELETE_URL = "rest/branch-utils/1.0/projects/{project}/repos/{reposlug}"

interface BitbucketClient {

    @RequestLine("GET $URL_PATH/pull-requests?state=merged")
    fun getMergedPullRequests(
        @Param("project") project: String,
        @Param("reposlug") slug: String
    ): ObjectNode

    @RequestLine("GET $URL_PATH/pull-requests")
    fun getOpenPullRequests(
        @Param("project") project: String,
        @Param("reposlug") slug: String
    ): ObjectNode

    @RequestLine("GET $URL_PATH/branches?details=true")
    fun getAllBranches(
        @Param("project") project: String,
        @Param("reposlug") slug: String
    ): ObjectNode

    @RequestLine("DELETE $BRANCH_DELETE_URL/branches")
    @Headers("Content-Type: application/json; charset=utf-8")
    fun deleteBranchById(
        @Param("project") project: String,
        @Param("reposlug") slug: String,
        branchDeleteRequestModel: BranchDeleteRequestModel
    ): ObjectNode
}
