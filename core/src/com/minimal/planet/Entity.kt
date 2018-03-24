package com.minimal.planet

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.ecs.Entity
import com.minimal.planet.game.HeroControl
import com.minimal.planet.game.ents.Hero

interface Script {
    fun update(me: MyEntity, timeStepSec: Float) {}
    fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {}
    fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {}
    fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {}
    fun debugDraw(me: MyEntity, renderer: ShapeRenderer) {}
}

object bulletScript : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if(other.contains(energy))
            other[energy] -= me[bullet].hitPoints
        me.dead = true
    }
}

object crashScript : Script {
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

class MyEntity : Entity() {
    val scripts = mutableListOf<Script>()
    fun add(s: Script) {
        scripts.add(s)
    }
}

class EntityBuilder() {
    val e = MyEntity()
    fun build(): MyEntity {
        return e
    }
    fun body(b: Body) {
        e.add(body, b)
        b.userData = e
    }
    fun energy(total: Float) {
        e.add(energy, Energy(total))
    }
    fun bullet(hitPoints: Float) {
        e.add(bullet, Bullet(hitPoints))
        e.add(bulletScript)
    }
    fun lifetime(lifeSec: Float) {
        e.add(lifetime, Lifetime(lifeSec))
    }
    fun gravity(value: Float) {
        e.add(gravity, value)
    }
    fun crash(threshold: Float = 1f, factor: Float = 1f) {
        e.add(crash, Crash(threshold, factor))
        e.add(crashScript)
    }
    fun cameraMagnet(magnet: Float) {
        e.add(cameraMagnet, magnet)
    }
    fun asteroid(level: Int) {
        e.add(asteroid, level)
    }
    fun hero(control: HeroControl) {
        e.add(hero, Hero(control))
    }
    fun script(script: Script) {
        e.add(script)
    }
}

fun MyEngine.entity(init: EntityBuilder.() -> Unit): MyEntity {
    val builder = EntityBuilder()
    builder.init()
    val e = builder.build()
    this.add(e)
    return e
}