package com.minimal.planet.game.ents

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.gdx.boxAngleRad
import com.minimal.gdx.polarToEuclid
import com.minimal.planet.*
import ktx.box2d.body
import ktx.box2d.filter

fun createInvader(angleRad: Float, height: Float, type: Int) {
    val pos = polarToEuclid(angleRad, height + ctx().level.worldRadius)
    ctx().engine.entity {
        body(ctx().world.body(DynamicBody) {
            position.set(pos)
            gravityScale = 1f
            fixedRotation = true
            linearDamping = 1f
            circle(0.5f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = invaderCat
                }
            }
        })
        energy(10f)
        sprite("invader-" + type, true)
        script(InvaderScript)
    }
}

object InvaderScript : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        me[body].linearVelocity = me[body].linearVelocity.set(0f, -1f).rotateRad(me[body].position.boxAngleRad())
    }
}