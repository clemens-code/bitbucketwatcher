package de.otto.bitbucketwatcher.client

import de.otto.bitbucketwatcher.model.TeamsMessage
import feign.Headers
import feign.RequestLine
import org.springframework.stereotype.Component

@Component
interface TeamsClient {

    @Headers("Content-Type: application/json; charset=utf-8")
    @RequestLine("POST /")
    fun postMessage(teamsMessage: TeamsMessage)
}
