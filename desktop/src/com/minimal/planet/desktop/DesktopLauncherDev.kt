package com.minimal.planet.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.minimal.arkanoid.wrap.ArkanoidDebugGame
import com.minimal.texturePacker

fun main(args: Array<String>) {
    texturePacker(args, "../../graphics/game", "./", "game")

    val config = LwjglApplicationConfiguration()
    if (args.contains("16:10")) {
        config.width = 400
        config.height = 640
    } else if (args.contains("15:9") || args.contains("5:3")) {
        config.width = 384
        config.height = 640
    } else {
        // aspect ratio 16:9, most popular ~74%, see: https://hwstats.unity3d.com/mobile/display-android.html
        config.width = 360
        config.height = 640
    }
    //		new LwjglApplication(new Planet(), config);
    //		new LwjglApplication(new ArkanoidGame(), config);
    LwjglApplication(ArkanoidDebugGame(), config)
    ParamsReloader("params.properties")
}
