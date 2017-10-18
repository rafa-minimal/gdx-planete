package com.minimal.arkanoid.game.script

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.entity.MyEntity

class ShakeScript(val ctx: Context) : Script {
    val threshold = .5f
    val scale = .01f

    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        val totalImpulse = impulse.normalImpulses.sum() * scale
        if (totalImpulse > threshold) {
            val normalImpulse = contact.worldManifold.normal.scl(totalImpulse)
            ctx.cameraSystem.shake(normalImpulse)
        }
    }
}