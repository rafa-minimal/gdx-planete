package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.minimal.boxang
import com.minimal.ecs.System
import com.minimal.minus
import com.minimal.norm
import com.minimal.times
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.filter
import ktx.math.plus
import ktx.math.vec2
import kotlin.experimental.xor

class Lemingo() {
}

object angleControlScript : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        val error = me[body].position.boxang() - me[body].angle
        me[body].applyTorque(error * 10f, false)
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

    private val vmax = 20f
    private var pidProportional = 5f

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
                    bullet(ctx, body.position + vec2(0f, 0.61f).rotateRad(leader.angle),
                            body.linearVelocity + vec2(0f, 30f).rotateRad(leader.angle))
                }
            }
            if (!leader.fireMode || leader.running) {
                if (left || right) {
                    body.linearDamping = 0.1f
                }
                if (left) {
                    /*if (leader.side == 1) {
                        leader.flip()
                    }*/
                    leader.running = true
                    val f = (-vmax - body.tangentVelocity()) * pidProportional
                    val force = body.tangentVector().scl(f)
                    body.applyForceToCenter(force, true)
                } else if (right) {
                    /*if (leader.side == -1) {
                        leader.flip()
                    }*/
                    leader.running = true
                    val f = (vmax - body.tangentVelocity()) * pidProportional
                    val force = body.tangentVector().scl(f)
                    body.applyForceToCenter(force, true)
                } else {
                    leader.running = false
                    body.linearDamping = 0.5f
                }
            }

            if (leader.up.justPressed()) {
                val normal = body.position.norm()
                if (ctx.world.querySquare(body.position - normal * 1f, 0.2f, all xor hero)) {

                    body.applyForceToCenter(body.position.norm().scl(400f), true)
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

    private val vmax = 10f
    private var pidProportional = 3f

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