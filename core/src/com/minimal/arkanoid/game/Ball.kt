package com.minimal.arkanoid.game

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.fx.ScaleScript
import com.minimal.arkanoid.game.script.JointBreakScript
import com.minimal.arkanoid.game.script.Script
import com.minimal.fx.SnakeTail
import com.minimal.utils.maskBits
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter
import ktx.math.vec2

val NEW_BALL_PRIORITY = 1
val BALL_PRIORITY = 0

val activateBall: (Context, MyEntity) -> Unit = {ctx: Context, ent: MyEntity ->
    ent[ball].priority = BALL_PRIORITY
    for (fix in ent[body].fixtureList) {
        fix.maskBits = all
    }

    if (Params.ball_respawn_mode == "after_taken") {
        Actions.schedule(Params.ball_respawn_after_taken_delay) {
            if (ctx.balls > 0) {
                createBall(ctx)
                ctx.balls--
            }
        }
    }
}

fun createBall(ctx: Context) {
    val pos = vec2(ctx.level.width / 2, Params.player_y + Params.player_range)

    val body = ctx.world.body(DynamicBody) {
        position.set(pos)
        linearDamping = 0f
        gravityScale = 0.5f
        circle(0.5f) {
            density = 1f
            restitution = Params.ball_restitution
            friction = 0f
            filter {
                categoryBits = com.minimal.arkanoid.game.default
                maskBits = cat.range
            }
        }
    }
    val joint = ctx.baseBody.distanceJointWith(body) {
        localAnchorA.set(pos)
        frequencyHz = 4f
        dampingRatio = 1f
        length = 0f
    }

    ctx.engine.entity {
        body(body)
        ball(NEW_BALL_PRIORITY)
        // bullet(5f) // BallScript spełnia tę rolę
        texture(ctx.atlas.findRegion("circle"), 1f, 1f, scale = 0f, color = Params.color_ball)
        script(ScaleScript(1f))
        tail(SnakeTail(TextureRegion(ctx.tailTex), 0.5f, 60))
        script(RespawnScript(ctx))
        script(BallSpeedLimit)
        script(BallScript())
        print("threshold: " + Params.ball_joint_threshold)
        script(JointBreakScript(ctx, joint, Params.ball_joint_threshold, activateBall))
        //script(SpeedScaleScript)
    }
}

object BallSpeedLimit : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        if (me[body].linearVelocity.y < -20f) {
            me[body].linearVelocity.y = -20f
        }
    }
}

class RespawnScript(val ctx: Context) : Script {
    override fun beforeDestroy(me: MyEntity) {
        if (Params.ball_respawn_mode == "after_death") {
            if (ctx.balls > 0) {
                createBall(ctx)
                ctx.balls--
            }
        }
    }
}

class BallScript : Script {
    val threshold = 1f
    val factor = 5f
    var autodestructionTimer = 0f

    fun autodestruction(me: MyEntity, timeStepSec: Float) {
        if (me[ball].priority == NEW_BALL_PRIORITY) {
            return
        }
        if(me[body].linearVelocity.len2() < 0.5f * 0.5f) {
            autodestructionTimer += timeStepSec
            if (autodestructionTimer > Params.ball_autodestruction_timeout) {
                me.dead = true
                // todo: [particle] explosion
            }
        } else {
            autodestructionTimer = 0f
        }
    }

    override fun update(me: MyEntity, timeStepSec: Float) {
        autodestruction(me, timeStepSec)
    }

    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        if(other.contains(energy)) {
            val force = impulse.normalImpulses.sum()
            if (force > threshold) {
                other[energy] -= factor * force
            }
        }
    }
}