package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.Context
import com.minimal.ecs.System

class HudSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.stage.act(timeStepSec)
        ctx.stage.draw()
    }
}