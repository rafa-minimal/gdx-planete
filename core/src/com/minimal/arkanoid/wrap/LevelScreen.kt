package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.minimal.arkanoid.Persistent
import com.minimal.gdx.alphaButton
import com.minimal.gdx.justPressed

class LevelScreen : Screen {
    private val stage: Stage = Stage(WrapCtx.viewport, WrapCtx.batch)

    var isPlay = false

    val rootTable: Table

    var currentLevel = Persistent.getLastLevel()

    val levelPreview = LevelPreview(currentLevel.toString())
    val levelLabel = Label("Level " + currentLevel, WrapCtx.skin)

    init {
        val unit = Gdx.graphics.height/8f
        val skin = WrapCtx.skin

        rootTable = Table(skin)
        rootTable.pad(10f)
        rootTable.setFillParent(true)

        rootTable.add("Arkanoid", "default").prefHeight(unit).row()
        rootTable.add("by minimal", "small").prefHeight(unit/2).row()


        rootTable.add(levelLabel).expand().row()

        val play = alphaButton(skin, "play") { isPlay = true }
        val left = alphaButton(skin, "left") { prevLevel() }
        val right = alphaButton(skin, "right") { nextLevel() }

        val bottom = Table()
        bottom.align(Align.bottom)
        bottom.add(left).size(unit)
        bottom.add(play).size(2*unit).expandX()
        bottom.add(right).size(unit)
        rootTable.add(bottom).fillX().row()

        stage.addActor(rootTable)
    }

    private fun nextLevel() {
        currentLevel++
        levelLabel.setText("Level " + currentLevel.toString())
        levelPreview.nextLevel(currentLevel.toString())
    }

    private fun prevLevel() {
        currentLevel = Math.max(1, currentLevel - 1)
        levelLabel.setText("Level " + currentLevel.toString())
        levelPreview.prevLevel(currentLevel.toString())
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

        if (Keys.LEFT.justPressed()) {
            prevLevel()
        }
        if (Keys.RIGHT.justPressed()) {
            nextLevel()
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