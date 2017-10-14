package com.minimal.arkanoid.game.system

import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context

class ScriptSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.engine.ents.forEach {
            e ->
            e.scripts.forEach {
                s -> s.update(e, timeStepSec)
                if (e.dead) {
                    s.beforeDestroy(e)
                }
            }
        }
    }
}