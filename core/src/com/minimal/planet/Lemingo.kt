package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.minimal.ecs.System
import ktx.box2d.body
import ktx.box2d.box
import ktx.math.plus
import ktx.math.vec2

class Lemingo(val canJumpSensor: Fixture) {
    var canJump = 0
}

object canJumpSensorScript : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (contact.fixtureA == me[lemingo].canJumpSensor || contact.fixtureB == me[lemingo].canJumpSensor)
            me[lemingo].canJump++
    }

    override fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (contact.fixtureA == me[lemingo].canJumpSensor || contact.fixtureB == me[lemingo].canJumpSensor)
            me[lemingo].canJump--
    }
}

class LemingoLeader(val up: Int = Keys.UP,
                    val left: Int = Keys.LEFT,
                    val right: Int = Keys.RIGHT,
                    val fire: Int = Keys.A) {
    var fireMode = false
    var angle = 45.deg()
    var running = false
}

object LemingoDebugDraw : Script {
    val from = vec2()
    val to = vec2()
    override fun debugDraw(me: MyEntity, renderer: ShapeRenderer) {
        renderer.color.set(Color.WHITE)
        renderer.line(
                from.set(me[body].position),
                to.set(me[body].position) + vec2(0f, 1f).rotateRad(me[leader].angle))

    }
}

class LemingoLeaderSystem(val ctx: Context) : System {
    val family = ctx.engine.family(leader, body)
    val lemingos = ctx.engine.family(lemingo, body)


    override fun update(timeStepSec: Float) {
        var leaderFound = false
        family.foreach {
            e, leader, body ->
            if (leader.fire.justPressed()) {
                leader.fireMode = true
            }
            val left = leader.left.pressed() && !leader.right.pressed()
            val right = leader.right.pressed() && !leader.left.pressed()
            if (leader.fireMode) {
                if (left && !leader.running) {
                    leader.angle += 5.deg()
                } else if (right && !leader.running) {
                    leader.angle -= 5.deg()
                }
                if (!leader.fire.pressed()) {
                    leader.fireMode = false
                    bullet(ctx, body.position + vec2(0f, 0.5f).rotateRad(leader.angle),
                            body.linearVelocity + vec2(0f, 30f).rotateRad(leader.angle))
                }
            }
            if (!leader.fireMode || leader.running) {
                if (left) {
                    /*if (leader.side == 1) {
                        leader.flip()
                    }*/
                    leader.running = true
                    val f = (-vmax - body.linearVelocity.x) * pidProportional
                    body.applyForceToCenter(f, 0f, true)
                } else if (right) {
                    /*if (leader.side == -1) {
                        leader.flip()
                    }*/
                    leader.running = true
                    val f = (vmax - body.linearVelocity.x) * pidProportional
                    body.applyForceToCenter(f, 0f, true)
                } else {
                    leader.running = false
                }
            }

            if (e[lemingo].canJump > 0) {
                if (leader.up.justPressed()) {
                    //body.linearVelocity.set(body.linearVelocity.x, 50f)
                    body.applyForceToCenter(0f, 400f, true)
                    //body.applyLinearImpulse(0f, 100f, 0f, 0f, true)
                }
            }
            leaderFound = true
        }
        if (!leaderFound) {
            lemingos.first {
                entity, lemingo, body ->
                entity.add(leader, LemingoLeader())
                entity.add(LemingoDebugDraw)
                entity.add(cameraMagnet, 10f)
            }
        }
    }
}

class LemingoSystem(val ctx: Context) : System {
    val leaders = ctx.engine.family(leader, body)
    val lemingos = ctx.engine.family(lemingo, body)

    fun getLeader(): Body? {
        var leaderBody: Body? = null
        leaders.first {
            entity, leader, body ->
            leaderBody = body
        }
        return leaderBody
    }

    override fun update(timeStepSec: Float) {
        var lead = getLeader()
        if (lead != null) {
            lemingos.foreach {
                lemingo, body ->
                val diff = lead.position.x - body.position.x
                val right = diff > 3f
                val left = diff < -3f
                if (left) {
                    /*if (leader.side == 1) {
                        leader.flip()
                    }*/
                    val f = (-vmax - body.linearVelocity.x) * pidProportional
                    body.applyForceToCenter(f, 0f, true)
                } else if (right) {
                    /*if (leader.side == -1) {
                        leader.flip()
                    }*/
                    val f = (vmax - body.linearVelocity.x) * pidProportional
                    body.applyForceToCenter(f, 0f, true)
                } else {
                }
            }
        }
    }
}

private val vmax = 20f
private var pidProportional = 5f

fun lemingo(ctx: Context, pos: Vector2) {
    val body = ctx.world.body(DynamicBody) {
        position.set(pos)
        fixedRotation = true
        circle(radius = 0.5f) {
            density = 1f
            restitution = 0.1f
        }
    }

    val canJump = body.box(width = 0.5f, height = 0.2f, position = vec2(0f, -0.5f)) {
        isSensor = true
    }

    val e = entity {
        body(body)
        lemingo(canJump)
    }
    e.scripts.add(canJumpSensorScript)
    ctx.engine.add(e)
}