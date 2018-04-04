package com.minimal.planet.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.minimal.arkanoid.wrap.ArkanoidDebugGame

fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.width = 400
    config.height = 640
    LwjglApplication(ArkanoidDebugGame(), config)
}