package com.minimal.arkanoid.game.fx

import com.badlogic.gdx.math.Interpolation.SwingOut
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.texture

class ScaleScript(val duration: Float) : Script {
    val interpol = SwingOut(1f)
    var x = 0f

    override fun update(me: MyEntity, timeStepSec: Float) {
        x += timeStepSec
        if(x < duration) {
            val scale = interpol.apply(x / duration)
            me[texture].scaleX = scale
            me[texture].scaleY = scale
        } else {
            me.scriptsToRemove.add(this)
            me[texture].scaleX = 1f
            me[texture].scaleY = 1f
        }
    }
}

class BoxTweeningScript(val duration: Float) : Script {
    val interpol = SwingOut(1f)
    var x = 0f

    override fun update(me: MyEntity, timeStepSec: Float) {
        x += timeStepSec
        if(x < duration) {
            val tween = interpol.apply(x / duration)
            me[texture].scaleX = tween
            me[texture].scaleY = tween
            me[texture].pos.set(0f, (1 - tween) * 20f)
            me[texture].angleDeg = (1 - tween) * 90f
        } else {
            me.scriptsToRemove.add(this)
            me[texture].scaleX = 1f
            me[texture].scaleY = 1f
            me[texture].pos.setZero()
            me[texture].angleDeg = 0f
        }
    }
}