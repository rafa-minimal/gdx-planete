package com.minimal.arkanoid.game.system

import com.badlogic.gdx.Input.Keys
import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context
import com.minimal.planet.justPressed
import com.minimal.utils.BoxPainter

class WorldRenderSystem(val ctx: Context) : System {
    var render = false
    val painter = BoxPainter(ctx)
    override fun update(timeStepSec: Float) {
        if (Keys.D.justPressed()) {
            render = !render
        }
        if (render) {
            ctx.debugRenderer.render(ctx.world, ctx.worldCamera.combined)
            // painter.render(ctx.world, ctx.worldCamera.combined)
        }
    }
}