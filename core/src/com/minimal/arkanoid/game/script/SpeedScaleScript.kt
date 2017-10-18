package com.minimal.arkanoid.game.script

import com.minimal.arkanoid.game.body
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.texture

object SpeedScaleScript : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {

        val x = me[body].linearVelocity.x
        // see fooplot.com
        // 0.5 - 1/(abs(x)+2)
        val dx = 0.5f - 1/(Math.abs(x)+2)
        val y = me[body].linearVelocity.y
        val dy = 0.5f - 1/(Math.abs(y)+2)

        me[texture].scaleX = 1f + dx
        me[texture].scaleY = 1f + dy
    }
}