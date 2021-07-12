package common

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
internal class BitbucketConstants(
    @Value("\${bitbucket.base-url}") private val url: String,
    @Value("\${bitbucket.project-key}") private val key: String,
    @Value("\${bitbucket.repo-slug}") private val slug: String
) {

    val baseUrl = url
    val projectKey = key
    val repoSlug = slug
}
