package bitbucketwatcher

import client.BitbucketClient
import client.TeamsClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import feign.Feign
import feign.Request
import feign.auth.BasicAuthRequestInterceptor
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
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

    @Bean
    fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor {
        return BasicAuthRequestInterceptor(bitbucketUser, bitbucketPassword)
    }

    @ConditionalOnProperty("bitbucket.base-url")
    @Bean
    internal fun bitbucketClient(
        @Value("\${bitbucket.base-url}") connectorUrl: String,
        @Value("\${bitbucket.readTimeout}") readTimeout: Long,
        @Value("\${bitbucket.connectTimeout}") connectTimeout: Long
    ): BitbucketClient =
        Feign.builder().run {
            encoder(JacksonEncoder())
            decoder(JacksonDecoder())
            options(Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, true))
            target(BitbucketClient::class.java, connectorUrl)
        }

    @ConditionalOnProperty("teams.url")
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
            options(Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, true))
            target(TeamsClient::class.java, connectorUrl)
        }
}
