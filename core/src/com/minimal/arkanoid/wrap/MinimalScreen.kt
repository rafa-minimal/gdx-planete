package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Align

class MinimalScreen : ScreenAdapter() {

    override fun show() {
//        SoundSystem.on(SoundEvent.Intro)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val batch = WrapCtx.batch
        val font = WrapCtx.font
        batch.setProjectionMatrix(WrapCtx.camera.combined)

        batch.begin()
        font.setColor(1f, 1f, 1f, 1f)
        font.draw(batch, "minimal", 0f,
                Gdx.graphics.height / 2 + font.getCapHeight() / 2,
                Gdx.graphics.width.toFloat(), Align.center, false)
        batch.end()
    }
}