package com.minimal.arkanoid.game.system

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context

class DebugRenderSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.renderer.setProjectionMatrix(ctx.worldCamera.combined)
        ctx.renderer.begin(Line)
        ctx.engine.ents.forEach {
            e ->
            e.scripts.forEach {
                it.debugDraw(e, ctx.renderer)
            }
        }
        ctx.renderer.end()
    }
}