package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.Context
import com.minimal.ecs.System

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
            for(s in e.scriptsToRemove) {
                e.scripts.remove(s)
            }
            e.scriptsToRemove.clear()
        }
    }
}