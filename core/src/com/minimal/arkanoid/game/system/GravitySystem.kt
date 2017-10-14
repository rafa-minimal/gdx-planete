package com.minimal.arkanoid.game.system

import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.body
import com.minimal.arkanoid.game.gravity
import ktx.math.minus

class GravitySystem(val ctx: Context) : System {
    val factor = 1f
    val planets = ctx.engine.family(body, gravity)
    val bodies = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        planets.foreach { planet, gravity ->
            bodies.foreach { body ->
                val vec = planet.position - body.position
                val len = vec.len()
                body.applyForceToCenter(vec.nor().scl(body.gravityScale * gravity / len), false)
            }
        }
    }
}