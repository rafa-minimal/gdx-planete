package com.minimal.arkanoid.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.entity.entity
import com.minimal.ecs.System
import com.minimal.utils.minus
import com.minimal.utils.plus
import com.minimal.utils.vec
import ktx.box2d.*
import ktx.math.vec2
import kotlin.experimental.xor

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

fun createPlayer(ctx: Context, width: Float, height: Float, baseBody: Body) {
    val pos = vec2(width / 2, height / 6)
    val playerRadius = 0.5f
    val playerRange = 3f

    val body = ctx.world.body(DynamicBody) {
        position.set(pos)
        linearDamping = 0.5f
        fixedRotation = true
    }

    val mainFixture = body.circle(playerRadius) {
        density = 2f
        filter {
            categoryBits = default
            maskBits = 0
        }
    }
    val rangeFixture = body.circle(playerRange) {
        isSensor = true
        filter {
            categoryBits = default
            maskBits = all xor static
        }
    }

    val player = ctx.engine.entity {
        body(body)
        player(rangeFixture)
        script(PlayerRangeScript)
        script(PowerUpCollector(ctx))
    }

    val limit = width/2 - playerRadius

    baseBody.prismaticJointWith(body) {
        localAnchorA.set(pos)
        localAxisA.set(1f, 0f)
        enableLimit = true
        lowerTranslation = -limit
        upperTranslation = limit
    }
}

class PowerUpCollector(val ctx: Context) : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (other.contains(pup)) {
            when (other.get(pup)) {
                Diamond -> {
                    other.dead = true
                }
            }
        }
    }
}