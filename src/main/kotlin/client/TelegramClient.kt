package io.github.clemenscode.bitbucketwatcher.client

import feign.Headers
import feign.RequestLine
import io.github.clemenscode.bitbucketwatcher.model.TelegramMessage
import org.springframework.stereotype.Component

@Component
internal interface TelegramClient {

    @Headers("Content-Type: application/json; charset=utf-8")
    @RequestLine("POST /sendMessage")
    fun postMessage(message: TelegramMessage)
}
