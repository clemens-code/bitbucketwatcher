package io.github.clemenscode.bitbucketwatcher.client

import feign.Headers
import feign.RequestLine
import io.github.clemenscode.bitbucketwatcher.model.TeamsMessage
import org.springframework.stereotype.Component

@Component
interface TeamsClient {

    @Headers("Content-Type: application/json; charset=utf-8")
    @RequestLine("POST")
    fun postMessage(teamsMessage: TeamsMessage)
}
