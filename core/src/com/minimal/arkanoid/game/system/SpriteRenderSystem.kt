package com.minimal.arkanoid.game.system

import com.badlogic.gdx.physics.box2d.Body
import com.minimal.arkanoid.game.*
import com.minimal.ecs.System
import com.minimal.utils.rad_deg

class SpriteRenderSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body, texture)
    val family2 = ctx.engine.family(parent, texture)

    override fun update(timeStepSec: Float) {
        ctx.batch.projectionMatrix.set(ctx.worldCamera.combined)
        ctx.batch.begin()
        family.foreach { body, texture ->
            draw(body, texture)
        }
        family2.foreach { parent, texture ->
            draw(parent.parent.get(body), texture)
        }
        ctx.batch.end()
    }

    fun draw(body: Body, texture: Texture) {
        ctx.batch.setColor(texture.color)
        val pos = body.position.add(texture.pos)
        val angle = body.angle.rad_deg()
        ctx.batch.draw(texture.texture,
                pos.x - texture.width/2, pos.y - texture.height/2,
                texture.width/2, texture.height/2,
                texture.width, texture.height,
                texture.scaleX, texture.scaleY,
                angle)
    }
}