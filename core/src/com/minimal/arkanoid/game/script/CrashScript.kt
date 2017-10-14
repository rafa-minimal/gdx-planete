package com.minimal.arkanoid.game.script

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.arkanoid.game.crash
import com.minimal.arkanoid.game.energy
import com.minimal.arkanoid.game.entity.MyEntity

object CrashScript : Script {
    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        val force = impulse.normalImpulses.sum() + impulse.tangentImpulses.sum()
        if (force >= me[crash].threshold) {
            if(me.contains(energy))
                me[energy] -= force * me[crash].factor
            else
                me.dead = true
        }
    }
}

