package com.minimal.planet.desktop

import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearNearest
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.io.File

private fun doPack(args: Array<String>): Boolean {
    if(args.contains("-f")) {
        println("force = true (flaga '-f'), pakujemy")
        return true
    }
    val target = File("./atlas.atlas")
    if (!target.exists()) {
        println("Nie ma pliku 'atlas.atlas', pakujemy")
        return true
    }

    val source = File("../../images")
    if (source.lastModified() > target.lastModified()) {
        println("Tekstury są nowsze od atlasu, pakujemy")
        return true
    } else {
        println("Tekstury są starsze od atlasu, pomijam")
        return false
    }
}

fun texturePacker(args: Array<String>) {
    if (doPack(args)) {
        val settings = TexturePacker.Settings()
        settings.maxWidth = 1024
        settings.maxHeight = 1024
        settings.filterMag = Linear
        // Aka. GL_LINEAR_MIPMAP_NEAREST - Chooses the mipmap that most closely matches the size of the pixel being
        // textured and uses the GL_LINEAR criterion (a weighted average of the four texture elements that are closest
        // to the center of the pixel) to produce a texture value.
        settings.filterMin = MipMapLinearNearest
        settings.paddingX = 4
        settings.paddingY = 4
        TexturePacker.process(settings, "../../images", "./", "atlas")
    }
}