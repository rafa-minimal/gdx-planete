package com.minimal.planet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.minimal.ecs.ComponentTag
import com.minimal.ecs.System
import ktx.math.minus
import ktx.math.unaryMinus
import ktx.math.vec2

class WorldSystem(val ctx: Context) : System, ContactListener {
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

    init {
        ctx.world.setContactListener(this)
    }

    override fun update(timeStepSec: Float) {
        val worldStep = Math.min(timeStepSec, 1 / 60f)
        ctx.world.step(worldStep, 8, 3)
        ctx.timeMs += (worldStep * 1000).toInt()
    }
}

class BodyDisposeSystem(val engine: MyEngine) : System {
    val family = engine.family(body)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, body ->
            if (ent.dead) {
                body.world.destroyBody(body)
            }
        }
    }
}

class EnergySystem(val engine: MyEngine) : System {
    val family = engine.family(energy)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, energy ->
            if (energy.energy <= 0f) {
                ent.dead = true
            }
        }
    }
}

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (C1) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                c1 ->
                action(c1)
            }
        }
    }
}

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (MyEntity, C1) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                ent, c1 ->
                action(ent, c1)
            }
        }
    }
}

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (MyEntity, C1, Float) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                ent, c1 ->
                action(ent, c1, timeStepSec)
            }
        }
    }
}

fun LifetimeSystem(engine: MyEngine) = system(engine, lifetime) {
    ent, lifetime, timeStepSec ->
    lifetime.lifetime -= timeStepSec
    if (lifetime.lifetime <= 0) {
        ent.dead = true
    }
}

/*class LifetimeSystem(val engine: MyEngine) : System {
    val family = engine.family(lifetime)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, lifetime ->
            lifetime.lifetime -= timeStepSec
            if (lifetime.lifetime <= 0) {
                ent.dead = true
            }
        }
    }
}*/

fun Int.pressed(): Boolean {
    return Gdx.input.isKeyPressed(this)
}


fun Int.justPressed(): Boolean {
    return Gdx.input.isKeyJustPressed(this)
}

class EdgeForceSystem(val ctx: Context) : System {
    val factor = 1f
    val family = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        family.foreach { body ->
            val outside = body.position.len() - ctx.level.worldRadius
            if (outside > 0) {
                val force = -body.position.nor().scl(outside * factor)
                body.applyForceToCenter(force, true)
            }
        }
    }
}

class GravitySystem(val ctx: Context) : System {
    val factor = 1f
    val planets = ctx.engine.family(body, gravity)
    val bodies = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        bodies.foreach { body ->
            planets.foreach { planet, gravity ->
                val vec = planet.position - body.position
                val len = vec.len()
                body.applyForceToCenter(vec.nor().scl(gravity/len),true)
            }
        }
    }
}

class WorldRenderSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.debugRenderer.render(ctx.world, ctx.worldCamera.combined)
    }
}

fun Vector2.rnd(radius: Float): Vector2 {
    val rad = MathUtils.random(radius)
    val phi = MathUtils.random(MathUtils.PI2)
    return set(rad * MathUtils.cos(phi), rad * MathUtils.sin(phi))
}

class AsteroidSpawnSystem(val ctx: Context) : System {
    val pos = vec2()
    lateinit var createAsteroid: () -> Unit

    init {
        createAsteroid = {
            asteroid(ctx, level = MathUtils.random(2));
            Actions.schedule(MathUtils.random(5f, 10f), createAsteroid)
        }
        Actions.schedule(MathUtils.random(5f, 10f), createAsteroid)
    }

    override fun update(timeStepSec: Float) {
        if(Keys.NUM_0.justPressed()) {
            asteroid(ctx, level = 0);
        }
        if(Keys.NUM_1.justPressed()) {
            asteroid(ctx, level = 1);
        }
        if(Keys.NUM_2.justPressed()) {
            asteroid(ctx, level = 2);
        }
    }
}

class CameraSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body, cameraMagnet)
    val pos = vec2()

    override fun update(timeStepSec: Float) {
        pos.setZero()
        family.foreach { body, magnet ->
            // todo: średnia ważona
            ctx.worldCamera.position.set(body.position)
            ctx.worldCamera.update()
        }
    }
}

private fun Vector3.set(vec: Vector2) {
    this.set(vec.x, vec.y, 0f)
}

class DebugRenderSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.renderer.setProjectionMatrix(ctx.worldCamera.combined)
        ctx.renderer.begin(ShapeType.Line)
        ctx.engine.ents.forEach {
            e -> e.scripts.forEach {
                it.debugDraw(e, ctx.renderer)
            }
        }
        ctx.renderer.end()
    }
}