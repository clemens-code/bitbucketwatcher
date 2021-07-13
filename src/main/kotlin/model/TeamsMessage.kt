package io.github.clemenscode.bitbucketwatcher.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TeamsMessage(
    @JsonProperty("@context")
    val context: String = "https://schema.org/extensions",
    @JsonProperty("@type")
    val type: String = "MessageCard",
    val themeColor: String = "0072C6",
    var title: String,
    var text: String
)
