package com.minimal.arkanoid.game.system

import com.minimal.ecs.System
import com.minimal.gdx.set
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.body
import com.minimal.arkanoid.game.cameraMagnet
import ktx.math.vec2

class CameraSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body, cameraMagnet)
    val pos = vec2()

    override fun update(timeStepSec: Float) {
        pos.setZero()
        family.foreach { body, magnet ->
            // todo: średnia ważona
            ctx.worldCamera.position.set(body.position)
            ctx.worldCamera.up.x = body.position.x
            ctx.worldCamera.up.y = body.position.y
            ctx.worldCamera.update()
        }
    }
}