package com.minimal.planet.desktop

import com.badlogic.gdx.graphics.Texture.TextureFilter.*
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
        // Aka. GL_LINEAR_MIPMAP_NEAREST - Chooses the mipmap that most closely matches the size of the pixel being
        // textured and uses the GL_LINEAR criterion (a weighted average of the four texture elements that are closest
        // to the center of the pixel) to produce a texture value.
        settings.filterMin = MipMapLinearNearest
        TexturePacker.process(settings, "../../images", "./", "atlas")
    } else {
        println("Tekstury są starsze od atlasu, pomijam")
    }
}