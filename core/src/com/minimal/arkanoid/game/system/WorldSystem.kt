package com.minimal.arkanoid.game.system

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.ecs.System

class WorldSystem(val ctx: Context) : System, ContactListener {
    init {
        ctx.world.setContactListener(this)
    }

    override fun update(timeStepSec: Float) {
        // Symulujemy dokładnie taki czas jaki upłynął pomiędzy klatkami, czyli jeśli fps spadnie (zwolni)
        // to i tak czas symulacji będzie płynął równo
        // Jeżeli spadnie poniżej 30 fps, krok symulacji mógłby być za duży, więc będziemy działać tak, jakby było 30 fps
        val worldStep = ctx.timeScale * Math.min(timeStepSec, 0.033333f)
        ctx.world.step(worldStep, 8, 3)
        ctx.timeMs += (worldStep * 1000).toInt()
    }

    override fun beginContact(contact: Contact?) {
        if (contact != null) {
            val firstEntity = contact.fixtureA.body.userData as MyEntity
            val secondEntity = contact.fixtureB.body.userData as MyEntity
            firstEntity.scripts.forEach {
                it.beginContact(firstEntity, secondEntity, contact)
            }
            secondEntity.scripts.forEach {
                it.beginContact(secondEntity, firstEntity, contact)
            }
        }
    }

    override fun endContact(contact: Contact?) {
        if (contact != null) {
            val firstEntity = contact.fixtureA.body.userData as MyEntity
            val secondEntity = contact.fixtureB.body.userData as MyEntity
            firstEntity.scripts.forEach {
                it.endContact(firstEntity, secondEntity, contact)
            }
            secondEntity.scripts.forEach {
                it.endContact(secondEntity, firstEntity, contact)
            }
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        if (contact != null && impulse != null) {
            val firstEntity = contact.fixtureA.body.userData as MyEntity
            val secondEntity = contact.fixtureB.body.userData as MyEntity
            firstEntity.scripts.forEach {
                it.postSolve(firstEntity, secondEntity, contact, impulse)
            }
            secondEntity.scripts.forEach {
                it.postSolve(secondEntity, firstEntity, contact, impulse)
            }
        }
    }
}