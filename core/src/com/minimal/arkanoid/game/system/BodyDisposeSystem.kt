package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.MyEngine
import com.minimal.ecs.System
import com.minimal.arkanoid.game.body

class BodyDisposeSystem(val engine: MyEngine) : System {
    val family = engine.family(body)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, body ->
            if (ent.dead) {
                body.world.destroyBody(body)
            }
        }
    }
}