package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.fx.BoxTweeningScript
import com.minimal.arkanoid.game.fx.FadeOutScript
import com.minimal.arkanoid.game.script.CrashScript
import com.minimal.arkanoid.game.script.JointBreakScript
import com.minimal.arkanoid.game.script.Script
import com.minimal.utils.maskBits
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter
import ktx.collections.isEmpty

object BoxDeadEffectScript : Script {
    override fun beforeDestroy(me: MyEntity) {
        me.dead = false
        val duration = 2f
        me[lifetime] = Lifetime(duration)
        me.scriptsToAdd.add(FadeOutScript(duration))
        me[energy] = null
        me.scriptsToRemove.add(this)
        for (fix in me[body].fixtureList) {
            fix.maskBits = 0
        }

        // usunięcie węzłów nie jest trywialne, trzeba jakoś dezaktywować JointBreakScript
        /*me.scriptsToRemove.addAll(me.scripts)
        for(joint in me[body].jointList) {
            joint.joint.bodyA.world.destroyJoint(joint.joint)
        }*/
    }
}

fun box(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = default
                }
            }
        })
        energy(10f)
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
    }
}

fun floor(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = default
                }
            }
        })
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
    }
}

fun house(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = cat.house
                }
            }
        })
        energy(1f)
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
        box() // house - żeby zliczać, czy wszystkie stoją
    }
}

val onBreak: (Context, MyEntity) -> Unit = { ctx: Context, ent: MyEntity ->
    for (fix in ent[body].fixtureList) {
        fix.restitution = Params.ball_restitution
    }
    if (ent[body].jointList.isEmpty()) {
        ent.add(ball, Ball(BOX_PRIORITY))
        ent[body].linearDamping = 0f
    }
}

fun boxNaZawiasach(ctx: Context, x: Float, y: Float) {
    val bod = ctx.world.body(DynamicBody) {
        position.set(x, y)
        linearDamping = 0.5f
        angularDamping = 0.5f
        box(Params.box_width, Params.box_height) {
            density = 0.2f
            restitution = Params.box_restitution
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
        if (Params.box_energy > 0f) {
            energy(Params.box_energy)
            crash(1f, 1f)
            script(CrashScript)
        }
        texture(ctx.atlas.findRegion("box"), Params.box_render_width, Params.box_render_height, scale = 0f, color = Params.color_box)
        script(BoxTweeningScript(1f))
        script(JointBreakScript(ctx, jl, Params.box_joint_threshold, onBreak))
        script(JointBreakScript(ctx, jr, Params.box_joint_threshold, onBreak))
        script(BoxDeadEffectScript)
        box()
    }
}

class BoxDiamondScript(val ctx: Context) : Script {
    override fun beforeDestroy(me: MyEntity) {
        createDiamond(ctx, me.get(body).position)
    }
}