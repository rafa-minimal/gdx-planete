package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.ecs.Entity
import com.minimal.ecs.System
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter

class Lemingo() {
}

class Player(val rangeFixture: Fixture,
             val up: Int = Keys.UP,
             val left: Int = Keys.LEFT,
             val right: Int = Keys.RIGHT,
             val fire: Int = Keys.A) {
    val entsInRange = ArrayList<MyEntity>()
    var firePressed = false
    var joint: DistanceJoint? = null
}

object PlayerRangeScript : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        val player = me[player]
        if (player.rangeFixture == contact.fixtureA || player.rangeFixture == contact.fixtureB) {
            player.entsInRange.add(other)
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
            if (player.fire.justPressed() and player.entsInRange.isNotEmpty()) {
                val otherBody = player.entsInRange[0][body]
                player.joint = b.distanceJointWith(otherBody) {
                    frequencyHz = 2f
                    dampingRatio = 0f
                    length = b.position.dst(otherBody.position)
                }
                player.firePressed = true
            }
            if (player.firePressed and !player.fire.pressed()) {
                ctx.world.destroyJoint(player.joint)
                player.firePressed = false
            }
            val left = player.left.pressed() && !player.right.pressed()
            val right = player.right.pressed() && !player.left.pressed()

            val v =
            if (left) {
                -vmax
            } else if (right) {
                vmax
            } else {
                0f
            }
            val f = (v - b.linearVelocity.x) * pidProportional
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