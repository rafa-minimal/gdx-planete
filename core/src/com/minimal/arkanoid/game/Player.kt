package com.minimal.arkanoid.game

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.script.SpeedScaleScript
import com.minimal.ecs.System
import com.minimal.utils.minus
import com.minimal.utils.vec
import ktx.box2d.*
import ktx.math.vec2
import kotlin.experimental.xor

val MAX_FORCE = 80f

class Player(val rangeFixture: Fixture,
             val input: PlayerControl) {
    val entsInRange = ArrayList<MyEntity>()
    var fireHold = false
    var joint: DistanceJoint? = null
    var ball: MyEntity? = null
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
            if (player.input.fireJustPressed and player.entsInRange.isNotEmpty()) {
                var other = player.entsInRange.find { ent -> ent.contains(ball) }
                if (other == null) {
                    other = player.entsInRange[0]
                }
                if (player.joint != null) {
                    ctx.world.destroyJoint(player.joint)
                    player.joint = null
                    player.ball = null
                }

                player.ball = other
                player.joint = b.distanceJointWith(other[body]) {
                    frequencyHz = 4f
                    dampingRatio = 0.3f
                    //length = b.position.dst(otherBody.position)
                    length = 3f
                }
                player.fireHold = true
            }
            if (player.fireHold and !player.input.fire) {
                ctx.world.destroyJoint(player.joint)
                player.joint = null
                player.ball = null
                player.fireHold = false
            }
            if (player.joint != null) {
                val ball = vec(player.joint!!.bodyB.position)
                val tan = (ball - b.position).rotate90(1).nor()
                val tangentSpeed = player.joint!!.bodyB.linearVelocity.dot(tan)
                val force = tan.scl(Math.signum(tangentSpeed) * 10f)
                player.joint!!.bodyB.applyForceToCenter(force, true)
            }

            val v = when {
                player.input.left -> -vmax
                player.input.right -> vmax
                else -> 0f
            }
            val f = (v - b.linearVelocity.x) * pidProportional * b.mass
            MathUtils.clamp(f, -MAX_FORCE, MAX_FORCE)
            b.applyForceToCenter(f, 0f, true)
        }
    }
}

fun createPlayer(ctx: Context, width: Float, playerHeight: Float, baseBody: Body) {
    val pos = vec2(width / 2, playerHeight / 6)
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
        player(rangeFixture, ctx.playerControl)
        texture(ctx.atlas.findRegion("circle2"), 1f, 1f)
        script(PlayerRangeScript)
        script(PowerUpCollector(ctx))
        script(SpeedScaleScript)
    }

    /*val playerCircle = ctx.engine.entity {
        texture(ctx.atlas.findRegion("range"), 6f, 6f)
        parent(player)
    }*/

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