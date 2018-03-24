package com.minimal.planet

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.ecs.Entity
import com.minimal.planet.game.HeroControl
import com.minimal.planet.game.ents.Hero
import com.minimal.planet.game.systems.Sprite
import ktx.collections.GdxArray

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
        me.die()
    }
}

object crashScript : Script {
    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        val force = impulse.normalImpulses.sum() + impulse.tangentImpulses.sum()
        if (force >= me[crash].threshold) {
            if(me.contains(energy))
                me[energy] -= force * me[crash].factor
            else
                me.die()
        }
    }
}

class MyEntity : Entity() {
    val scripts = mutableListOf<Script>()
    var children: GdxArray<MyEntity>? = null
    fun add(s: Script) {
        scripts.add(s)
    }

    fun addChild(e: MyEntity) {
        if (children == null) {
            children = GdxArray(1)
        }
        children!!.add(e)
    }

    override fun die() {
        super.die()
        children?.map {e -> e.die()}
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
    fun sprite(texName: String, faceUp: Boolean = false) {
        /*val t = wrap().atlas.findRegion(texName) ?: throw IllegalStateException("Texture not found in game atlas: " + texName)
        val s = Sprite(t)
        s.setOrigin(t.regionWidth / 2f, t.regionHeight / 2f)
        s.setSize(size, size)
        e.add(sprite, s)*/
        e.add(sprite, Sprite(texName, faceUp))
    }

    fun parent(p: MyEntity) {
        e.add(parent, p)
        p.addChild(e)
    }
}

fun MyEngine.entity(init: EntityBuilder.() -> Unit): MyEntity {
    val builder = EntityBuilder()
    builder.init()
    val e = builder.build()
    this.add(e)
    return e
}