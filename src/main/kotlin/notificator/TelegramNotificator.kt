package io.github.clemenscode.bitbucketwatcher.notificator

import io.github.clemenscode.bitbucketwatcher.client.TelegramClient
import io.github.clemenscode.bitbucketwatcher.model.TelegramMessage
import io.github.clemenscode.bitbucketwatcher.pullrequest.checker.PullRequestMessage
import org.springframework.beans.factory.annotation.Value

internal class TelegramNotificator(
private val telegramClient: TelegramClient,
@Value("\${telegram.chat-id}") private val chatId: String
) : Notificator {

    override fun publishMessage(message: PullRequestMessage) {
        telegramClient.postMessage(TelegramMessage(chat_id = chatId, text = message.message))
    }
}