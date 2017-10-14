package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.children
import com.minimal.ecs.System

class ParentChildSystem(ctx: Context) : System {
    val family = ctx.engine.family(children)

    override fun update(timeStepSec: Float) {
        family.foreach { entity, children ->
            if (entity.dead) {
                for (c in children.children) {
                    c.dead = true
                }
            }
        }
    }
}