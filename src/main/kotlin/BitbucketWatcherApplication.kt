package io.github.clemenscode.bitbucketwatcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class BitbucketWatcherApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator") // no performance issue here
    runApplication<BitbucketWatcherApplication>(*args)
}
