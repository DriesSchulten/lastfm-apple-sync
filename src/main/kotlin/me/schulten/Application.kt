package me.schulten

import io.ktor.application.Application
import me.schulten.config.config

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
  config()
}