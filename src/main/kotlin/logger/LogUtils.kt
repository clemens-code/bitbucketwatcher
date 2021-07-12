package logger

import org.slf4j.LoggerFactory

fun getLogger(classInstance: Class<out Any>) = LoggerFactory.getLogger(classInstance) ?: error("Could not get Logger!")
