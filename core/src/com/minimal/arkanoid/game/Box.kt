package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.fx.BoxTweeningScript
import com.minimal.arkanoid.game.script.JointBreakScript
import com.minimal.arkanoid.game.script.Script
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter
import ktx.collections.isEmpty

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

val onBreak: (Context, MyEntity) -> Unit = {ctx: Context, ent: MyEntity ->
    if (ent[body].jointList.isEmpty()) {
        ent.add(ball, Ball(4))
    }
}

fun boxNaZawiasach(ctx: Context, x: Float, y: Float) {
    val bod = ctx.world.body(DynamicBody) {
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
    val jl = ctx.baseBody.distanceJointWith(bod) {
        length = 0f
        localAnchorA.set(x - 0.5f, y)
        localAnchorB.set(-0.5f, 0f)
        frequencyHz = 4f
        dampingRatio = 0.8f
    }
    val jr = ctx.baseBody.distanceJointWith(bod) {
        length = 0f
        localAnchorA.set(x + 0.5f, y)
        localAnchorB.set(0.5f, 0f)
        frequencyHz = 4f
        dampingRatio = 0.8f
    }
    ctx.engine.entity {
        body(bod)
        texture(ctx.atlas.findRegion("box"), Params.box_render_width, Params.box_render_height, scale = 0f, color = Params.color_box)
        script(BoxTweeningScript(1f))
        script(JointBreakScript(ctx, jl, Params.box_joint_threshold, onBreak))
        script(JointBreakScript(ctx, jr, Params.box_joint_threshold, onBreak))
        box()
    }
}

class BoxDiamondScript(val ctx: Context) : Script {
    override fun beforeDestroy(me: MyEntity) {
        createDiamond(ctx, me.get(body).position)
    }
}