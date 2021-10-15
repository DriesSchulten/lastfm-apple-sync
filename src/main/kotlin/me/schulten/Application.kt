package me.schulten

import io.ktor.application.Application
import kotlinx.serialization.ExperimentalSerializationApi
import me.schulten.config.config

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@ExperimentalSerializationApi
fun Application.module(testing: Boolean = false) {
  config()
}