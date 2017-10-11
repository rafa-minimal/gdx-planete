package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport

object WrapCtx {
    val batch = SpriteBatch()

    val mux = InputMultiplexer()

    val camera = OrthographicCamera()
    val viewport = ScreenViewport()
    //val viewport = FitViewport()

    val font: BitmapFont
    val bigFont: BitmapFont
    val atlas: TextureAtlas
    val skin: Skin

    init {
        // On some devices it might start in portrait mode and then quickly rotate to landscape
        val height = Math.min(Gdx.graphics.height, Gdx.graphics.width)

        /*if (height <= 480) {
            loadFonts(17, 26)
            loadAtlas("mdpi")
        } else if (height <= 480 * 1.5) {
            loadFonts(22, 34)
            loadAtlas("hdpi")
        } else if (height <= 480 * 2) {
            loadFonts(34, 51)
            loadAtlas("xhdpi")
        } else if (height <= 480 * 3) {
            loadFonts(44, 68)
            loadAtlas("xxhdpi")
        } else {
            loadFonts(44, 68)
            loadAtlas("xxxhdpi")
        }*/

        atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
        font = BitmapFont(Gdx.files.internal("fonts/century-gothic-17.fnt"))
        bigFont = BitmapFont(Gdx.files.internal("fonts/century-gothic-26.fnt"))

        skin = Skin(atlas)
        skin.add("small-font", font)
        skin.add("default-font", bigFont)
        skin.load(Gdx.files.internal("skin.json"))
    }

    /*private fun loadAtlas(dir: String) {
        atlas = TextureAtlas(Gdx.files.internal(dir + "/asteroids.atlas"))
    }

    private fun loadFonts(smallSize: Int, bigSize: Int) {
        font = BitmapFont(Gdx.files.internal("fonts/century-gothic-$smallSize.fnt"))
        bigFont = BitmapFont(Gdx.files.internal("fonts/century-gothic-$bigSize.fnt"))
    }*/

    fun dispose() {
        batch.dispose()
        font.dispose()
        bigFont.dispose()
        atlas.dispose()
        skin.dispose()
    }
}