package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.gdx.alphaButton
import ktx.actors.then

class LevelFailedActor : Table(WrapCtx.skin) {
    init {
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        add("Game Over", "default").row()
        val buttons = Table(WrapCtx.skin)
        val unit = 0.2f * Gdx.graphics.width
        buttons.add(alphaButton(WrapCtx.skin, "arrow") {
            Gdx.app.log("Invaders", "retry")
            WrapEvent.set("retry")
            hide()
        }).size(unit)
        buttons.add(alphaButton(WrapCtx.skin, "arrow") {
            Gdx.app.log("Invaders", "restart")
            WrapEvent.set("restart")
            hide()
        }).size(unit)
        buttons.add(alphaButton(WrapCtx.skin, "arrow") {
            Gdx.app.log("Invaders", "quit")
            WrapEvent.set("quit")
            hide()
        }).size(unit)
        add(buttons)
    }

    private fun hide() {
        addAction(Actions.fadeOut(0.5f).then(Actions.removeActor()))
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        if (stage != null) {
            setPosition(0f, Gdx.graphics.height.toFloat())
            addAction(Actions.moveTo(0f, 0f, 0.5f, Interpolation.swingOut))
        }
    }
}