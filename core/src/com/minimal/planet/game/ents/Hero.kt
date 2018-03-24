package com.minimal.planet.game.ents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.ecs.System
import com.minimal.gdx.boxAngleRad
import com.minimal.minus
import com.minimal.norm
import com.minimal.normAngleRad
import com.minimal.planet.*
import com.minimal.planet.game.HeroControl
import com.minimal.planet.game.Params
import com.minimal.planet.game.ents.HeroState.Shoot
import com.minimal.times
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.plus
import ktx.math.vec2
import kotlin.experimental.xor

enum class HeroState {
    Stop,
    Run,
    Shoot
}

class Hero(val control: HeroControl) {
    /**
     * 0 - do góry, dodatnie - w lewo (CCW)
     */
    var angleRad: Float = -1.toRad()
    var direction: Int = 1
    var state: HeroState = HeroState.Stop
}

object HeroDebugDraw : Script {
    val from = vec2()
    val to = vec2()
    override fun debugDraw(me: MyEntity, renderer: ShapeRenderer) {
        renderer.color.set(Color.WHITE)
        val angle = me[hero].angleRad + me[body].position.boxAngleRad()
        renderer.line(
                from.set(me[body].position),
                to.set(me[body].position) + vec2(0f, 1f).rotateRad(angle))

    }
}

class HeroSystem : System {
    //val heroes = ctx().engine.family(hero, body)

    override fun update(timeStepSec: Float) {
        ctx().engine.family(hero, body).foreach { me, hero, body ->
            val left = if (hero.control.left) -1 else 0
            val right = if (hero.control.right) 1 else 0
            val move = left + right
            if (hero.control.fire) {
                hero.state = HeroState.Shoot
                when (move) {
                    -1 -> hero.angleRad += 5.toRad()
                    1 -> hero.angleRad -= 5.toRad()
                }
                hero.angleRad = hero.angleRad.normAngleRad()
                hero.direction = -Math.signum(hero.angleRad).toInt()

                val angle = body.position.boxAngleRad() + hero.angleRad
                bullet(body.position + vec2(0f, 0.61f).rotateRad(angle),
                        body.linearVelocity + vec2(0f, 30f).rotateRad(angle))
            } else {
                when (move) {
                    -1 -> {
                        val vver = body.linearVelocity.dot(body.position.nor())
                        val vhor = -Params.heroSpeed
                        val vlocal = Vector2(vhor, vver)

                        body.linearVelocity = vlocal.rotateRad(body.position.boxAngleRad())
                        hero.direction = -1
                        hero.state = HeroState.Run
                    }
                    1 -> {
                        val vver = body.linearVelocity.dot(body.position.nor())
                        val vhor = Params.heroSpeed
                        val vlocal = Vector2(vhor, vver)

                        body.linearVelocity = vlocal.rotateRad(body.position.boxAngleRad())
                        hero.direction = 1
                        hero.state = HeroState.Run
                    }
                    0 -> hero.state = HeroState.Stop
                }
            }
            if (hero.control.fire || move == 0) {
                body.linearDamping = Params.heroStopDamping
            }

            if (hero.control.jumpJustPressed) {
                val normal = body.position.norm()
                if (ctx().world.querySquare(body.position - normal * 1f, 0.2f, all xor heroCat)) {
                    body.applyForceToCenter(body.position.norm().scl(Params.heroJumpForce), true)
                }
            }
            me[sprite].scaleX = hero.direction.toFloat()
        }
    }
}

fun createHero(pos: Vector2, control: HeroControl) {
    val body = ctx().world.body(DynamicBody) {
        position.set(pos)
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

    val hero = ctx().engine.entity {
        body(body)
        hero(control)
        energy(5f)
        cameraMagnet(1f)
        script(HeroDebugDraw)
        sprite("hero-body-1", true)
    }

    ctx().engine.entity {
        sprite("pistol")
        parent(hero)
        script(UpdatePistolAngleScript)
    }
}

object UpdatePistolAngleScript : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        val hero = me[parent][hero]
        val body = me[parent][body]
        if (hero.state == Shoot) {
            if (hero.angleRad < 0f) {
                me[sprite].angleRad = hero.angleRad
                me[sprite].scaleX = 1f
            } else {
                me[sprite].angleRad = hero.angleRad
                me[sprite].scaleX = -1f
            }
            me[sprite].angleRad += body.position.boxAngleRad()
        } else {
            me[sprite].angleRad = body.position.boxAngleRad() - hero.direction * MathUtils.PI / 2
            me[sprite].scaleX = hero.direction.toFloat()
        }
    }
}