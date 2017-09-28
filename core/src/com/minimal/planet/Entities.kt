package com.minimal.planet

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody
import ktx.box2d.*
import ktx.math.vec2

private val asterVel = vec2()


val verticesArrays: Array<Array<Vector2>> = arrayOf(
        Array<Vector2>(5, { i -> vec2() }),
        Array<Vector2>(7, { i -> vec2() }),
        Array<Vector2>(9, { i -> vec2() }))

val verticesFloatArrays: Array<FloatArray> = arrayOf(
        FloatArray(5 * 2),
        FloatArray(7 * 2),
        FloatArray(9 * 2))

val asterSize: FloatArray = floatArrayOf(1f, 1.5f, 2f)

fun vertices(level: Int): FloatArray {
    val verticesArray = verticesFloatArrays[level]
    val step = MathUtils.PI2 / verticesArray.size
    var angle = 0f
    val vec = vec2()
    for (i in 0.rangeTo(verticesArray.size / 2 - 2)) {
        vec.set(0f, MathUtils.random(asterSize[level] * 0.8f, asterSize[level] * 1.2f))
        vec.rotateRad(angle)
        verticesArray[i * 2] = vec.x
        verticesArray[i * 2 + 1] = vec.y
        angle += step
    }
    return verticesArray
}

private val safePosition = vec2()

fun bullet(ctx: Context, pos: Vector2, vel: Vector2) {
    val e = ctx.engine.entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(pos)
                    linearVelocity.set(vel)
                    circle(radius = 0.1f) {
                        density = 2f
                        restitution = 1f
                        filter {
                            categoryBits = default
                            groupIndex = -2
                        }
                    }
                }
        )
        bullet(1f)
        lifetime(5f)
    }
}

fun planet(ctx: Context, radius: Float, pos: Vector2, omega: Float) {
    val e = ctx.engine.entity {
        body(
                ctx.world.body(KinematicBody) {
                    position.set(pos)
                    angularVelocity = omega
                    circle(radius = radius) {
                        restitution = 0f
                        filter {
                            categoryBits = planetCat
                            maskBits = all
                        }
                    }
                }
        )
        gravity(10f)
    }
}

fun edge(ctx: Context, from: Vector2, to: Vector2) {
    val e = ctx.engine.entity {
        body(
                ctx.world.body {
                    edge(from, to) {
                        restitution = 0.1f
                        friction = 0.9f
                    }
                }
        )
    }
}

class RespawnScript(val ctx: Context) : Script {
    override fun beforeDestroy(me: MyEntity) {
        createBall(ctx)
    }
}

fun createBall(ctx: Context) {
    ctx.engine.entity {
        body(ctx.world.body(DynamicBody) {
            //position.set(ctx.level.width/2, ctx.level.height/6 + 3)
            position.set(ctx.level.width/2, ctx.level.height/5 + 3)
            linearVelocity.set(10f, 10f)
            linearDamping = 0f
            gravityScale = 0.5f
            circle(0.5f) {
                density = 1f
                restitution = 1f
                friction = 0f
                filter {
                    categoryBits = default
                }
            }
        })
        ball()
        bullet(5f)
        script(RespawnScript(ctx))
        script(BallSpeedLimit)
        script(ballScript)
    }
}

object BallSpeedLimit : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        if (me[body].linearVelocity.y < -20f) {
            me[body].linearVelocity.y = -20f
        }
    }
}
