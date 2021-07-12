package de.otto.bitbucketwatcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BitbucketWatcherApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator") // no performance issue here
    runApplication<BitbucketWatcherApplication>(*args)
}
