package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.body
import com.minimal.arkanoid.game.tail
import com.minimal.ecs.System

class TailSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body, tail)

    override fun update(timeStepSec: Float) {
        family.foreach { body, tail ->
            //val alpha = body.linearVelocity.len() / 20f
            //tail.add(body.position, alpha)
            tail.add(body.position)
        }
    }
}

class TailRenderSystem(val ctx: Context) : System {
    val family = ctx.engine.family(tail)

    override fun update(timeStepSec: Float) {
        ctx.batch.projectionMatrix.set(ctx.worldCamera.combined)
        ctx.batch.begin()
        family.foreach { tail ->
            tail.draw(ctx.batch)
        }
        ctx.batch.end()

    }
}