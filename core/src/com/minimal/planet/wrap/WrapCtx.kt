package com.minimal.planet.wrap

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas

private var wrap: WrapCtx? = null

fun wrap(): WrapCtx {
    return wrap!!
}

fun wrap(newWrapCtx: WrapCtx) {
    wrap = newWrapCtx
}

open class WrapCtx {
    val atlas = TextureAtlas("game.atlas")
    val batch = SpriteBatch()
}