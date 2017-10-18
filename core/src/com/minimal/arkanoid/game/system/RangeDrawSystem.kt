package com.minimal.arkanoid.game.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.body
import com.minimal.arkanoid.game.player
import com.minimal.ecs.System
import com.minimal.gdx.render
import ktx.math.vec2

class RangeDrawSystem(val ctx: Context) : System {
    val family = ctx.engine.family(player, body)

    val circle = ctx.atlas.findRegion("circle")
    var effect: Interpolation? = Interpolation.swingOut
    var effectTime = 0f
    val N = 12

    var angle = 0f
    var omega = 1.5f

    val points = Array<Vector2>(N) { _ -> vec2() }
    val desired = Array<Vector2>(N) { _ -> vec2() }

    val tmp = vec2()
    val pos = vec2()

    override fun update(timeStepSec: Float) {
        var full_radius = 3f
        family.foreach { player, bod ->
            if (player.ball != null) {
                pos.set(player.ball!!.get(body).position)
                full_radius = 1.5f
            } else {
                pos.set(bod.position)
            }
        }
        angle += omega * timeStepSec
        if (angle >= MathUtils.PI2) {
            angle -= 2 * MathUtils.PI2
        }

        var radius = full_radius
        if (effect != null) {
            effectTime += timeStepSec
            radius = effect!!.apply(effectTime) * full_radius
            if (effectTime > 1f) {
                effect = null
            }
        }
        tmp.set(0f, radius).rotateRad(angle)
        for (point in points) {
            point.set(pos).add(tmp)
            tmp.rotateRad(MathUtils.PI2 / N)
        }

        ctx.batch.render(ctx.worldCamera, Color.WHITE) {
            for (point in points) {
                ctx.batch.draw(circle, point.x - 0.1f, point.y - 0.1f, 0.2f, 0.2f)
            }
        }
    }
}