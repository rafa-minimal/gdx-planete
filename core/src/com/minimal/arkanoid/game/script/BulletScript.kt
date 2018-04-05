package com.minimal.arkanoid.game.script

import com.badlogic.gdx.physics.box2d.Contact
import com.minimal.arkanoid.game.bullet
import com.minimal.arkanoid.game.energy
import com.minimal.arkanoid.game.entity.MyEntity

object BulletScript : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if(other.contains(energy)) {
            other[energy] -= me[bullet].hitPoints
        }
        me.dead = true
    }
}