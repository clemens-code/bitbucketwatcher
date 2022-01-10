package io.github.clemenscode.bitbucketwatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import feign.Feign
import feign.Request
import feign.Retryer
import feign.auth.BasicAuthRequestInterceptor
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import io.github.clemenscode.bitbucketwatcher.client.BitbucketClient
import io.github.clemenscode.bitbucketwatcher.client.TeamsClient
import io.github.clemenscode.bitbucketwatcher.client.TelegramClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

/**
 * Bean Configuration for the BitbucketWatcher
 */
@Configuration
@ConfigurationPropertiesScan
@ComponentScan
class BitbucketWatcherConfiguration(
    @Value("\${bitbucket.username}") private val bitbucketUser: String,
    @Value("\${bitbucket.password}") private val bitbucketPassword: String,
) {

    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
    }

    @ConditionalOnProperty(prefix = "bitbucket", name = ["base-url"])
    @Bean
    internal fun bitbucketClient(
        @Value("\${bitbucket.base-url}") connectorUrl: String,
        @Value("\${bitbucket.readTimeout}") readTimeout: Long,
        @Value("\${bitbucket.connectTimeout}") connectTimeout: Long
    ): BitbucketClient =
        Feign.builder().run {
            encoder(JacksonEncoder())
            decoder(JacksonDecoder())
            requestInterceptor(BasicAuthRequestInterceptor(bitbucketUser, bitbucketPassword, Charsets.UTF_8))
            options(Request.Options(connectTimeout, TimeUnit.SECONDS, readTimeout, TimeUnit.SECONDS, true))
            target(BitbucketClient::class.java, connectorUrl)
        }

    @ConditionalOnProperty(prefix = "teams", name = ["url"])
    @Bean
    internal fun teamsClient(
        @Value("\${teams.url}") connectorUrl: String,
        @Value("\${teams.readTimeout}") readTimeout: Long,
        @Value("\${teams.connectTimeout}") connectTimeout: Long
    ): TeamsClient =
        Feign.builder().run {
            encoder(JacksonEncoder(objectMapper))
            decoder(JacksonDecoder(objectMapper))
            decode404()
            options(Request.Options(connectTimeout, TimeUnit.SECONDS, readTimeout, TimeUnit.SECONDS, true))
            target(TeamsClient::class.java, connectorUrl)
        }

    @ConditionalOnProperty(prefix = "telegram", name = ["{token}"])
    @Bean
    internal fun telegramClient(
        @Value("\${telegram.url}") connectorUrl: String,
        @Value("\${telegram.readTimeout}") readTimeout: Long,
        @Value("\${telegram.connectTimeout}") connectTimeout: Long,
        @Value("\${telegram.token}") token: String,
    ): TelegramClient =
        Feign.builder().run {
            encoder(JacksonEncoder(objectMapper))
            decoder(JacksonDecoder(objectMapper))
            decode404()
            retryer(Retryer.NEVER_RETRY)
            options(Request.Options(connectTimeout, TimeUnit.SECONDS, readTimeout, TimeUnit.SECONDS, true))
            target(TelegramClient::class.java, "$connectorUrl$token")
        }
}
