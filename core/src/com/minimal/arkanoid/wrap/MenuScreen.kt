package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling.fit
import com.minimal.arkanoid.wrap.WrapCtx.batch
import com.minimal.gdx.alphaButton
import com.minimal.gdx.glClear
import com.minimal.gdx.justPressed

class MenuScreen : Screen {
    private val stage: Stage = Stage(WrapCtx.viewport, WrapCtx.batch)

    var isPlay = false

    val rootTable: Table

    lateinit var m_fbo: FrameBuffer
    lateinit var m_fboRegion: TextureRegion

    init {
        val unit = Gdx.graphics.height / 8f
        val pad = Gdx.graphics.height / 8f / 8f
        val skin = WrapCtx.skin

        rootTable = Table(skin)
        rootTable.pad(pad)
        rootTable.setFillParent(true)

        val drawable = TextureRegionDrawable(WrapCtx.menu.findRegion("bricks"))
        val image = Image(drawable, fit)
        image.setColor(Color.WHITE)
        rootTable.add(image).row()

        rootTable.add("by minimal", "small").prefHeight(unit / 2).row()

        val playButton = alphaButton(skin, "play") { isPlay = true }
        rootTable.add(playButton).expand().row()

        val buttons = Table()

        val starButton = alphaButton(skin, "rate") { println("rate") }
        val infoButton = alphaButton(skin, "info") { println("info") }
        buttons.add(starButton).size(unit)
        buttons.add(infoButton).size(unit).row()
        rootTable.add(buttons).expandX().row()
        stage.addActor(rootTable)
    }

    override fun show() {
        isPlay = false
        WrapCtx.mux.addProcessor(stage)

        m_fbo = FrameBuffer(Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
        m_fboRegion = TextureRegion(m_fbo.getColorBufferTexture())
        m_fboRegion.texture.setFilter(Nearest, Nearest)
        m_fboRegion.flip(false, true)
    }

    override fun render(delta: Float) {
        m_fbo.begin()

        glClear(Color(0xffffff00.toInt()))
        //glClear(Color(0x3370ccff))
        stage.act(delta)


        /*val x = rootTable.getX()
        val y = rootTable.getY()
        rootTable.setPosition(x + 10f, y - 10f)
        blackBatch.setProjectionMatrix(stage.viewport.camera.combined)
        blackBatch.begin()
        rootTable.draw(blackBatch, 1f)
        blackBatch.end()

        rootTable.setPosition(x, y)*/

        //frameBuffer.begin()

        //glClear(Color(0x00000000))
        //stage.batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_SRC_COLOR)
        stage.batch.disableBlending()
        stage.draw()
        stage.batch.enableBlending()

        m_fbo.end()

        batch.begin()
        glClear(Color(0x3370ccff))
        batch.setColor(0f, 0f, 0f, 0.2f)
        batch.draw(m_fboRegion, 10f, -10f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.setColor(1f, 1f, 1f, 1f)
        //Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR)
        //Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR)
        batch.draw(m_fboRegion, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()


        if (Keys.NUM_1.justPressed()) {
            rootTable.setDebug(!rootTable.debug, false)
        }

        if (Keys.ENTER.justPressed()) {
            isPlay = true
        }
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {
        WrapCtx.mux.removeProcessor(stage)
    }

    override fun dispose() {
        stage.dispose()
    }
}