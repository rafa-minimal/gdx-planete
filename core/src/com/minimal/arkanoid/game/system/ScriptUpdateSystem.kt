package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.Context
import com.minimal.ecs.System

class ScriptUpdateSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.engine.ents.forEach {
            e ->
            e.scripts.forEach {
                s -> s.update(e, timeStepSec)
            }
            for(s in e.scriptsToRemove) {
                e.scripts.remove(s)
            }
            e.scriptsToRemove.clear()
        }
    }
}

class ScriptBeforeDestroySystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.engine.ents.forEach {
            e ->
            if (e.dead) {
                e.scripts.forEach {
                    s ->
                    s.beforeDestroy(e)
                }
            }
        }
    }
}