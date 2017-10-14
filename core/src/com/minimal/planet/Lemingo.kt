package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.ecs.System
import com.minimal.minus
import com.minimal.plus
import com.minimal.vec
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter

class Lemingo() {
}

class Player(val rangeFixture: Fixture,
             val leftKey: Int = Keys.LEFT,
             val rightKey: Int = Keys.RIGHT,
             val fireKey: Int = Keys.A) {
    val entsInRange = ArrayList<MyEntity>()
    var fireHold = false
    var joint: DistanceJoint? = null

    var fire: Boolean = false
    var fireJustPressed: Boolean = false
    var left: Boolean = false
    var right: Boolean = false
}

object PlayerRangeScript : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        val player = me[player]
        if (player.rangeFixture == contact.fixtureA || player.rangeFixture == contact.fixtureB) {
            if (!other.contains(pup)) {
                player.entsInRange.add(other)
            }
        } else {
            // energia?
        }
    }

    override fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {
        val player = me[player]
        if (player.rangeFixture == contact.fixtureA || player.rangeFixture == contact.fixtureB) {
            player.entsInRange.remove(other)
        }
    }
}

class PlayerSystem(val ctx: Context) : System {
    val family = ctx.engine.family(player, body)

    private val vmax = 20f
    private var pidProportional = 10f

    override fun update(timeStepSec: Float) {
        family.foreach {
            e, player, b ->
            if (player.fireJustPressed and player.entsInRange.isNotEmpty()) {
                val otherBody = player.entsInRange[0][body]
                player.joint = b.distanceJointWith(otherBody) {
                    frequencyHz = 4f
                    dampingRatio = 0.3f
                    //length = b.position.dst(otherBody.position)
                    length = 3f
                }
                player.fireHold = true
            }
            if (player.fireHold and !player.fire) {
                ctx.world.destroyJoint(player.joint)
                player.joint = null
                player.fireHold = false
            }
            if (player.joint != null) {
                val ball = vec(player.joint!!.bodyB.position)
                val tan = (ball - b.position).rotate90(1).nor()
                val tangentSpeed = player.joint!!.bodyB.linearVelocity.dot(tan)
                val force = tan.scl(Math.signum(tangentSpeed) * 10f)
                player.joint!!.bodyB.applyForceToCenter(force, true)

                ctx.renderer.projectionMatrix = ctx.worldCamera.combined
                ctx.renderer.begin(Line)
                ctx.renderer.color = Color.RED
                ctx.renderer.line(player.joint!!.bodyB.position, player.joint!!.bodyB.position + tan)
                ctx.renderer.end()
            }

            val v = when {
                player.left -> -vmax
                player.right -> vmax
                else -> 0f
            }
            val f = (v - b.linearVelocity.x) * pidProportional * b.mass
            b.applyForceToCenter(f, 0f, true)
        }
    }
}

fun lemingo(ctx: Context, pos: Vector2) {
    val body = ctx.world.body(DynamicBody) {
        position.set(pos)
        gravityScale = 10f
        fixedRotation = true
        circle(radius = 0.5f) {
            density = 1f
            restitution = 0f
            friction = 0.1f
            filter {
                categoryBits = hero
            }
        }
    }

    val e = ctx.engine.entity {
        body(body)
        lemingo()
        energy(5f)
    }
    //e.scripts.add(angleControlScript)
}