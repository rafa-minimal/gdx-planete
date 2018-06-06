package com.minimal.fx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.minimal.gdx.glClear

/**
 * Używamy tego tak:
 * <pre>
 *     shadowRenderer.begin()
 *     // tutaj rysujemy
 *     // ważne! wyłączyć blending, bo w tle mamy zero alpha
 *     shadowRenderer.end()
 * </pre>
 */
class ShadowRenderer(val batch: SpriteBatch,
                     val width: Int = Gdx.graphics.width,
                     val height: Int = Gdx.graphics.height) {

    val frameBuffer: FrameBuffer
    val frameBufferTexture: TextureRegion

    val offset = Vector2(10f, -10f)

    init {
        frameBuffer = FrameBuffer(RGBA8888, width, height, false)
        frameBufferTexture = TextureRegion(frameBuffer.getColorBufferTexture())
        frameBufferTexture.texture.setFilter(Nearest, Nearest)
        frameBufferTexture.flip(false, true)
    }

    fun begin() {
        frameBuffer.begin()
        glClear(Color(0x00000000))
    }

    fun end() {
        frameBuffer.end()
        batch.begin()
        batch.setColor(0f, 0f, 0f, 0.2f)
        batch.draw(frameBufferTexture, offset.x, offset.y, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.setColor(1f, 1f, 1f, 1f)
        batch.draw(frameBufferTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()
    }

    fun resize() {

    }

    fun dispose() {
        frameBuffer.dispose()
    }
}