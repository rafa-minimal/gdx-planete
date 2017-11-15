package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.JointBreakScript
import com.minimal.arkanoid.game.script.Script
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter

fun boxOneShot(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(Params.box_width, Params.box_height) {
                density = 1f
                restitution = 0.4f
                filter {
                    categoryBits = com.minimal.arkanoid.game.default
                }
            }
        })
        energy(10f)
        texture(ctx.atlas.findRegion("box"), Params.box_render_width, Params.box_render_height, color = Params.color_box)
        box()
    }
}

fun boxDiament(ctx: Context, x: Float, y: Float) {
    boxOneShot(ctx, x, y).add(BoxDiamondScript(ctx))
}

fun boxNaZawiasach(ctx: Context, x: Float, y: Float, baseBody: Body) {
    val body = ctx.world.body(DynamicBody) {
        position.set(x, y)
        linearDamping = 0.5f
        angularDamping = 0.5f
        box(Params.box_width, Params.box_height) {
            density = 0.2f
            restitution = 1f
            filter {
                categoryBits = default
            }
        }
    }
    val jl = baseBody.distanceJointWith(body) {
        length = 0f
        localAnchorA.set(x - 0.5f, y)
        localAnchorB.set(-0.5f, 0f)
        frequencyHz = 4f
        dampingRatio = 0.8f
    }
    val jr = baseBody.distanceJointWith(body) {
        length = 0f
        localAnchorA.set(x + 0.5f, y)
        localAnchorB.set(0.5f, 0f)
        frequencyHz = 4f
        dampingRatio = 0.8f
    }
    ctx.engine.entity {
        body(body)
        texture(ctx.atlas.findRegion("box"), Params.box_render_width, Params.box_render_height, color = Params.color_box)
        script(JointBreakScript(ctx, jl))
        script(JointBreakScript(ctx, jr))
        box()
    }
}

class BoxDiamondScript(val ctx: Context) : Script {
    override fun beforeDestroy(me: MyEntity) {
        createDiamond(ctx, me.get(body).position)
    }
}