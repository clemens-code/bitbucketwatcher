package io.github.clemenscode.bitbucketwatcher.model

@Suppress("ConstructorParameterNaming")
internal data class TelegramMessage(
    val chat_id: String,
    val text: String = "",
    val parse_mode: String = "HTML",
)
