package com.minimal.planet.game

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.Sprite
import com.minimal.planet.ctx
import com.minimal.planet.wrap.wrap

class SinglePlayerControlsHud {

    val arrowLeft = wrap().atlas.createSprite("right")
    val arrowRight = wrap().atlas.createSprite("right")
    val arrowUp = wrap().atlas.createSprite("jump")
    val fireSprite = wrap().atlas.createSprite("fire")

    val textureSize: Int

    init {
        arrowLeft.setFlip(true, false)
        textureSize = arrowLeft.texture.height
    }

    fun render(delta: Float) {
        wrap().batch.setProjectionMatrix(ctx().hudCamera.combined)
        wrap().batch.begin()
        if (ctx().heroControl.left) {
            arrowLeft.draw(wrap().batch, 1f)
        } else {
            arrowLeft.draw(wrap().batch, 0.5f)
        }
        if (ctx().heroControl.right) {
            arrowRight.draw(wrap().batch, 1f)
        } else {
            arrowRight.draw(wrap().batch, 0.5f)
        }
        if (ctx().heroControl.jump) {
            arrowUp.draw(wrap().batch, 1.0f)
        } else {
            arrowUp.draw(wrap().batch, 0.5f)
        }
        if (ctx().heroControl.fire) {
            fireSprite.draw(wrap().batch, 1.0f)
        } else {
            fireSprite.draw(wrap().batch, 0.5f)
        }
        wrap().batch.end()
    }

    fun resize(width: Int, height: Int) {
        val controlSize = Math.min(textureSize, Math.min(width / 8, height / 6))

        arrowLeft.setBounds(0f, controlSize.toFloat(), controlSize.toFloat(), controlSize.toFloat())
        arrowRight.setBounds(controlSize.toFloat(), 0f, controlSize.toFloat(), controlSize.toFloat())
        arrowUp.setBounds((width - controlSize).toFloat(), controlSize.toFloat(), controlSize.toFloat(), controlSize.toFloat())
        fireSprite.setBounds((width - controlSize * 2).toFloat(), 0f, controlSize.toFloat(), controlSize.toFloat())
    }
}