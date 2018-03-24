package com.minimal.planet.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.minimal.ecs.System
import com.minimal.planet.ctx

interface HeroControl {
    val jump: Boolean
    val jumpJustPressed: Boolean
    val left: Boolean
    val right: Boolean
    val fire: Boolean
    fun update(deltaTimeSec: Float)
}

class SinglePlayerHeroControl : HeroControl {
    override var jump = false
    override var jumpJustPressed = false
    override var fire = false
    override var left = false
    override var right = false

    override fun update(deltaTimeSec: Float) {
        jumpJustPressed = false
        jump = false
        fire = false
        left = false
        right = false

        for (i in 0..4) {
            if (Gdx.input.isTouched(i)) {
                var x = Gdx.input.getX(i)
                val y = Gdx.graphics.height - Gdx.input.getY(i)
                if (x < Gdx.graphics.width / 2) {
                    if (y > x)
                        left = true
                    else
                        right = true
                }
                x = Gdx.graphics.width - Gdx.input.getX(i)
                if (x < Gdx.graphics.width / 2) {
                    if (y > x) {
                        jumpJustPressed = !jump
                        jump = true
                    } else {
                        fire = true
                    }
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            left = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            right = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            jumpJustPressed = !jump
            jump = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            fire = true
        }
    }
}

class InputUpdateSystem : System {
    override fun update(timeStepSec: Float) {
        ctx().heroControl.update(timeStepSec)
    }
}