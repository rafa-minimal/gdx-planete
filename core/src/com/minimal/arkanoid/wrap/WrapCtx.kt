package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport

object WrapCtx {
    val batch = SpriteBatch()

    val mux = InputMultiplexer()

    val camera = OrthographicCamera()
    val viewport = ScreenViewport()

    var font: BitmapFont
    var bigFont: BitmapFont
    val atlas: TextureAtlas
    val gameAtlas: TextureAtlas
    val skin: Skin

    init {
        // On some devices it might start in portrait mode and then quickly rotate to landscape
        val dim = Math.min(Gdx.graphics.height, Gdx.graphics.width)

        if (dim <= 480) {
            font = loadFont(17)
            bigFont = loadFont(26)
//            loadAtlas("mdpi")
        } else if (dim <= 480 * 1.5) {
            font = loadFont(22)
            bigFont = loadFont(34)
//            loadAtlas("hdpi")
        } else if (dim <= 480 * 2) {
            font = loadFont(34)
            bigFont = loadFont(51)
//            loadAtlas("xhdpi")
        } else if (dim <= 480 * 3) {
            font = loadFont(44)
            bigFont = loadFont(68)
//            loadAtlas("xxhdpi")
        } else {
            font = loadFont(44)
            bigFont = loadFont(68)
//            loadAtlas("xxxhdpi")
        }

        atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
        gameAtlas = TextureAtlas(Gdx.files.internal("game.atlas"))

        skin = Skin(atlas)
        skin.add("small-font", font)
        skin.add("default-font", bigFont)
        skin.load(Gdx.files.internal("skin.json"))

        addDrawables()
    }

    private fun addDrawables() {
        skin.add("right", skin.newDrawable("play"), Drawable::class.java)
        val left = TextureRegion(skin.getRegion("play"))
        left.flip(true, false)
        skin.add("left", TextureRegionDrawable(left), Drawable::class.java)
    }

    /*private fun loadAtlas(dir: String) {
        atlas = TextureAtlas(Gdx.files.internal(dir + "/asteroids.atlas"))
    }*/

    private fun loadFont(size: Int) = BitmapFont(Gdx.files.internal("fonts/century-gothic-$size.fnt"))

    fun dispose() {
        batch.dispose()
        font.dispose()
        bigFont.dispose()
        atlas.dispose()
        gameAtlas.dispose()
        skin.dispose()
    }

    fun resize(w: Float, h: Float) {
        viewport.setWorldSize(w, h)
    }
}