package com.minimal.arkanoid.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.minimal.ecs.System

interface HeroControl {
    val jump: Boolean
    val jumpJustPressed: Boolean
    val left: Boolean
    val right: Boolean
    val fire: Boolean
    val fireJustPressed: Boolean
    fun update(deltaTimeSec: Float)
}

class SinglePlayerHeroControl : HeroControl, InputAdapter() {
    override var jump = false
    override var jumpJustPressed = false
    override var fire = false
    override var fireJustPressed = false
    override var left = false
    override var right = false

    override fun update(deltaTimeSec: Float) {
        var _jump = false
        var _fire = false
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
                        _jump = true
                    } else {
                        _fire = true
                    }
                }
            }
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            left = true
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            right = true
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            jumpJustPressed = !jump
            _jump = true
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            fireJustPressed = !fire
            _fire = true
        }

        jumpJustPressed = !jump && _jump
        jump = _jump
        fireJustPressed = !fire && _fire
        fire = _fire
    }
}

class InputUpdateSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.heroControl.update(timeStepSec)
    }
}