package com.minimal.arkanoid.game

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import com.minimal.fx.SnakeTail
import ktx.box2d.body
import ktx.box2d.filter

fun createBall(ctx: Context) {
    ctx.engine.entity {
        body(ctx.world.body(DynamicBody) {
            //position.set(ctx.level.width/2, ctx.level.height/6 + 3)
            position.set(ctx.level.width / 2, ctx.level.height / 5 + 3)
            linearVelocity.set(10f, 10f)
            linearDamping = 0f
            gravityScale = 0.5f
            circle(0.5f) {
                density = 1f
                restitution = 1f
                friction = 0f
                filter {
                    categoryBits = com.minimal.arkanoid.game.default
                }
            }
        })
        ball()
        bullet(5f)
        texture(ctx.atlas.findRegion("circle"), 1f, 1f)
        tail(SnakeTail(TextureRegion(ctx.tailTex), 0.5f, 60))
        script(RespawnScript(ctx))
        script(BallSpeedLimit)
        script(BallScript)
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
        createBall(ctx)
    }
}

object BallScript : Script {
    val threshold = 1f
    val factor = 5f

    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        if(other.contains(energy)) {
            val force = impulse.normalImpulses.sum()
            if (force > threshold) {
                other[energy] -= factor * force
            }
        }
    }
}