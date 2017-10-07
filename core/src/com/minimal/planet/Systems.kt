package com.minimal.planet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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
import ktx.math.vec2

class PlayerControlUpdateSystem(val ctx: Context) : System {
    val family = ctx.engine.family(player)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, player ->
            val wasFire = player.fire
            player.fire = false
            player.left = false
            player.right = false

            for (i in 0..4) {
                if (Gdx.input.isTouched(i)) {
                    var x = Gdx.input.getX(i)
                    val y = Gdx.graphics.height - Gdx.input.getY(i)
                    if (x < Gdx.graphics.width / 2) {
                        if (y > x)
                            player.left = true

                        else
                            player.right = true
                    }
                    x = Gdx.graphics.width - Gdx.input.getX(i)
                    if (x < Gdx.graphics.width / 2) {
                        player.fire = true
                        player.fireJustPressed = !wasFire
                    }
                }
            }
            player.fire = player.fire or player.fireKey.pressed()
            player.fireJustPressed = player.fireJustPressed or player.fireKey.justPressed()
            player.left = player.left or player.leftKey.pressed()
            player.right = player.right or player.rightKey.pressed()
        }
    }
}

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

class GravitySystem(val ctx: Context) : System {
    val factor = 1f
    val planets = ctx.engine.family(body, gravity)
    val bodies = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        planets.foreach { planet, gravity ->
            bodies.foreach { body ->
                val vec = planet.position - body.position
                val len = vec.len()
                body.applyForceToCenter(vec.nor().scl(body.gravityScale * gravity / len), false)
            }
        }
    }
}

class WorldRenderSystem(val ctx: Context) : System {
    val painter = BoxPainter(ctx)
    override fun update(timeStepSec: Float) {
//        ctx.debugRenderer.render(ctx.world, ctx.worldCamera.combined)
        painter.render(ctx.world, ctx.worldCamera.combined)
    }
}

fun Vector2.rnd(radius: Float): Vector2 {
    val rad = MathUtils.random(radius)
    val phi = MathUtils.random(MathUtils.PI2)
    return set(rad * MathUtils.cos(phi), rad * MathUtils.sin(phi))
}

class CameraSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body, cameraMagnet)
    val pos = vec2()

    override fun update(timeStepSec: Float) {
        pos.setZero()
        family.foreach { body, magnet ->
            // todo: średnia ważona
            ctx.worldCamera.position.set(body.position)
            ctx.worldCamera.up.x = body.position.x
            ctx.worldCamera.up.y = body.position.y
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
            e ->
            e.scripts.forEach {
                it.debugDraw(e, ctx.renderer)
            }
        }
        ctx.renderer.end()
    }
}

class ScriptSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        ctx.engine.ents.forEach {
            e ->
            e.scripts.forEach {
                s -> s.update(e, timeStepSec)
                if (e.dead) {
                    s.beforeDestroy(e)
                }
            }
        }
    }
}

class CleanUpSystem(val ctx: Context) : System {
    val family = ctx.engine.family(body)
    override fun update(timeStepSec: Float) {
        family.foreach { ent, body ->
            if (body.position.y < -5f) {
                ent.dead = true
            }
        }
    }
}