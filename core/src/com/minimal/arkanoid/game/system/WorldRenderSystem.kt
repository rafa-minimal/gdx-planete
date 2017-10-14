package com.minimal.arkanoid.game.system

import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context
import com.minimal.utils.BoxPainter

class WorldRenderSystem(val ctx: Context) : System {
    val painter = BoxPainter(ctx)
    override fun update(timeStepSec: Float) {
        ctx.debugRenderer.render(ctx.world, ctx.worldCamera.combined)
//        painter.render(ctx.world, ctx.worldCamera.combined)
    }
}