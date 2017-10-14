package com.minimal.planet.desktop

import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearLinear
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.io.File

fun texturePacker(args: Array<String>) {
    val force = args.contains("-f")

    val source = File("../../images")
    val target = File("./")
    if (force || source.lastModified() > target.lastModified()) {
        if (force)
            println("force = true (flaga '-f'), pakujemy")
        else
            println("Tekstury są nowsze od atlasu, pakujemy")
        val settings = TexturePacker.Settings()
        settings.maxWidth = 1024
        settings.maxHeight = 1024
        settings.filterMag = Linear
        settings.filterMin = MipMapLinearLinear
        TexturePacker.process(settings, "../../images", "./", "atlas")
    } else {
        println("Tekstury są starsze od atlasu, pomijam")
    }
}