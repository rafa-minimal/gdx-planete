package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.gdx.alphaButton
import com.minimal.gdx.justPressed

class LevelScreen : Screen {
    private val stage: Stage = Stage(WrapCtx.viewport, WrapCtx.batch)

    var isPlay = false

    val rootTable: Table

    var currentLevel = "random"

    val levelPreview = LevelPreview(currentLevel)

    init {
        val unit = Gdx.graphics.height/8f
        val skin = WrapCtx.skin

        rootTable = Table(skin)
        rootTable.pad(10f)
        rootTable.setFillParent(true)

        rootTable.add("Arkanoid", "default").prefHeight(unit).colspan(3).row()
        rootTable.add("by minimal", "small").prefHeight(unit/2).colspan(3).row()

        val playButton = alphaButton(skin, "play") { isPlay = true }
        rootTable.add(playButton).expand().colspan(3).row()

        stage.addActor(rootTable)
    }

    override fun show() {
        isPlay = false
        WrapCtx.mux.addProcessor(stage)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        levelPreview.act(delta)
        levelPreview.draw()

        stage.act(delta)
        stage.draw()

        if (Keys.NUM_1.justPressed()) {
            rootTable.setDebug(!rootTable.debug, false)
        }

        if (Keys.ENTER.justPressed()) {
            isPlay = true
        }
    }

    override fun resize(width: Int, height: Int) {
        levelPreview.resize(width, height)
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