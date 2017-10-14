package com.minimal.arkanoid.game.system

import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.body

class CleanUpSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        family.foreach { ent, body ->
            if (body.position.y < -5f) {
                ent.dead = true
            }
        }
    }
}