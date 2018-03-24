package com.minimal.planet.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.minimal.planet.Planet
import com.minimal.texturePacker

object DesktopDev {
    @JvmStatic
    fun main(arg: Array<String>) {
        //texturePacker(arg, "../../graphics/menu", "./", "menu")
        texturePacker(arg, "../../graphics/game", "./", "game")

        val config = LwjglApplicationConfiguration()
        config.title = "Invaders"

        // Youtube preferred resolution for 16:9 aspect ratio
        config.width = 800
        config.height = 480

        if (arg.contains("-d")) {
            println("Debug mode (-d flag)")
            LwjglApplication(Planet(), config)
        } else if (arg.contains("-r")) {
            println("Recording mode (-r flag)")
            config.width = 1600
            config.height = 960
            LwjglApplication(Planet(), config)
        } else if (arg.contains("-b")) {
            println("Benchmark mode (-b flag)")
            LwjglApplication(Planet(), config)
        } else if (arg.contains("-bv")) {
            println("Benchmark verbose mode (-bv flag)")
            LwjglApplication(Planet(), config)
        } else {
            LwjglApplication(Planet(), config)
        }
    }
}