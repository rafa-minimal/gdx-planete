package com.minimal.planet.game.ents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.ecs.System
import com.minimal.gdx.rotateLeft
import com.minimal.gdx.rotateRight
import com.minimal.minus
import com.minimal.norm
import com.minimal.planet.*
import com.minimal.planet.game.HeroControl
import com.minimal.planet.game.Params
import com.minimal.times
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.plus
import ktx.math.vec2
import kotlin.experimental.xor

class Hero(val control: HeroControl) {
    var angle: Float = 45.toRad()
}

object LemingoDebugDraw : Script {
    val from = vec2()
    val to = vec2()
    override fun debugDraw(me: MyEntity, renderer: ShapeRenderer) {
        renderer.color.set(Color.WHITE)
        renderer.line(
                from.set(me[body].position),
                to.set(me[body].position) + vec2(0f, 1f).rotateRad(me[hero].angle))

    }
}

class HeroSystem : System {
    //val heroes = ctx().engine.family(hero, body)

    override fun update(timeStepSec: Float) {
        ctx().engine.family(hero, body).foreach { leader, body ->
            val left = if (leader.control.left) -1 else 0
            val right = if (leader.control.right) 1 else 0
            val move = left + right
            if (leader.control.fire) {
                when (move) {
                    -1 -> leader.angle += 5.toRad()
                    1 -> leader.angle -= 5.toRad()
                }
                bullet(body.position + vec2(0f, 0.61f).rotateRad(leader.angle),
                        body.linearVelocity + vec2(0f, 30f).rotateRad(leader.angle))
            } else {
                when (move) {
                    -1 -> {
                        val force = body.position.nor().rotateLeft().scl(Params.heroForce)
                        body.applyForceToCenter(force, true)
                        body.linearDamping = Params.heroMoveDamping
                    }
                    1 -> {
                        val force = body.position.nor().rotateRight().scl(Params.heroForce)
                        body.applyForceToCenter(force, true)
                        body.linearDamping = Params.heroMoveDamping
                    }
                }
            }
            if (leader.control.fire || move == 0) {
                body.linearDamping = Params.heroStopDamping
            }

            if (leader.control.jumpJustPressed) {
                val normal = body.position.norm()
                if (ctx().world.querySquare(body.position - normal * 1f, 0.2f, all xor heroCat)) {
                    body.applyForceToCenter(body.position.norm().scl(Params.heroJumpForce), true)
                }
            }
        }
    }
}

fun createHero(pos: Vector2, control: HeroControl) {
    val body = ctx().world.body(DynamicBody) {
        position.set(pos)
        // todo czy używamy gravity scale?
        gravityScale = 10f
        fixedRotation = true
        linearDamping = Params.heroStopDamping
        circle(radius = 0.5f) {
            density = 1f
            restitution = 0f
            friction = 0f
            filter {
                categoryBits = heroCat
            }
        }

    }

    ctx().engine.entity {
        body(body)
        hero(control)
        energy(5f)
        script(LemingoDebugDraw)
    }
}