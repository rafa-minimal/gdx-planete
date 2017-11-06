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
import com.minimal.arkanoid.Tuning

object WrapCtx {
    val batch = SpriteBatch()

    val mux = InputMultiplexer()

    val camera = OrthographicCamera()
    val viewport = ScreenViewport()

    var font: BitmapFont
    var bigFont: BitmapFont
    val atlas: TextureAtlas
    val skin: Skin

    val tuning = Tuning("tuning.properties")

    init {
        // On some devices it might start in portrait mode and then quickly rotate to landscape
        val dim = Math.min(Gdx.graphics.height, Gdx.graphics.width)

        if (dim <= 480) {
            loadFonts(17, 26)
//            loadAtlas("mdpi")
        } else if (dim <= 480 * 1.5) {
            loadFonts(22, 34)
//            loadAtlas("hdpi")
        } else if (dim <= 480 * 2) {
            loadFonts(34, 51)
//            loadAtlas("xhdpi")
        } else if (dim <= 480 * 3) {
            loadFonts(44, 68)
//            loadAtlas("xxhdpi")
        } else {
            loadFonts(44, 68)
//            loadAtlas("xxxhdpi")
        }

        atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
        font = BitmapFont(Gdx.files.internal("fonts/century-gothic-17.fnt"))
        bigFont = BitmapFont(Gdx.files.internal("fonts/century-gothic-26.fnt"))

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

    private fun loadFonts(smallSize: Int, bigSize: Int) {
        font = BitmapFont(Gdx.files.internal("fonts/century-gothic-$smallSize.fnt"))
        bigFont = BitmapFont(Gdx.files.internal("fonts/century-gothic-$bigSize.fnt"))
    }

    fun dispose() {
        batch.dispose()
        font.dispose()
        bigFont.dispose()
        atlas.dispose()
        skin.dispose()
    }
}