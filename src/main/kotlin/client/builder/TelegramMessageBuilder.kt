package io.github.clemenscode.bitbucketwatcher.client.builder

import io.github.clemenscode.bitbucketwatcher.model.TelegramMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Suppress("ConstructorParameterNaming")
@Component
internal class TelegramMessageBuilder(
    @Value("\${telegram.chat-id}") private val chatId: String
) {
    fun buildTelegramMessage(message: String): TelegramMessage {
        return TelegramMessage(chatId, message)
    }
}
