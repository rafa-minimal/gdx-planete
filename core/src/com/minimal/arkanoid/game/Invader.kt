package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import com.minimal.utils.rnd
import ktx.box2d.body
import ktx.box2d.filter
import kotlin.experimental.xor

fun invader(ctx: Context, posx: Float, posy: Float, type: Int) : MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(DynamicBody) {
            position.set(posx, posy)
            gravityScale = 0f
            fixedRotation = true
            box(0.6f, 0.6f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = cat.invader
                    maskBits = cat.all xor cat.invaderBullet
                }
            }
        })
        energy(10f)
        sprite("circle")
        script(InvaderScript(ctx))
    }
}

class InvaderScript(val ctx: Context) : Script {
    var nextShiftSec = 4f
    var nextFireSec = rnd(5f, 15f)
    // r, dl, l, dr, r, dl...
    var dir = "r"

    override fun update(me: MyEntity, timeStepSec: Float) {
        nextShiftSec -= timeStepSec
        if (nextShiftSec <= 0f) {
            when(dir) {
                "r" -> switch("dl")
                "dl" -> switch("l")
                "l" -> switch("dr")
                "dr" -> switch("r")
            }
        }

        val v = Params.invader_velocity
        when(dir) {
            "r" -> me[body].setLinearVelocity(v, 0f)
            "dl" -> me[body].setLinearVelocity(0f, -v)
            "dr" -> me[body].setLinearVelocity(0f, -v)
            "l" -> me[body].setLinearVelocity(-v, 0f)
        }

        fireControl(me, timeStepSec)
    }

    private fun fireControl(me: MyEntity, timeStepSec: Float) {
        nextFireSec -= timeStepSec
        if (nextFireSec <= 0f) {
            invaderBullet(ctx, me[body].position, 0f, -5f)
            nextFireSec = rnd(5f, 15f)
        }
    }

    private fun switch(newDir: String) {
        dir = newDir
        if(dir[0] == 'd') {
            nextShiftSec = 2f
        } else {
            nextShiftSec = 4f
        }
    }
}