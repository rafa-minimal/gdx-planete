package com.minimal.planet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody
import com.minimal.ecs.Family2
import ktx.box2d.*
import ktx.math.plus
import ktx.math.vec2
import kotlin.experimental.xor

private val rocketCat = 1.toShort()
private val bulletCat = 2.toShort()
private val asteroidCat = 4.toShort()
private val planetCat = 8.toShort()

private val all = 65535.toShort()

private val asterVel = vec2()

fun asteroid(ctx: Context, level: Int) {
    val pos = randomSafePosition(ctx, ctx.engine.family(tank, body))
    asterVel.rnd(5f)
    val e = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(pos)
                    linearVelocity.set(asterVel)
                    polygon(vertices(level)) {
                        density = 1f
                        restitution = 0.1f
                        filter {
                            categoryBits = asteroidCat
                            maskBits = all
                        }
                    }
                }
        )
        asteroid(level)
    }
    ctx.engine.add(e)
}

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

fun randomSafePosition(ctx: Context, family: Family2<MyEntity, TankControl, Body>): Vector2? {
    val safeRadius2 = 5f * 5f

    val tryPosition: (Vector2) -> Boolean = {
        position: Vector2 ->
        family.foreach { rocketControl, body ->
            if (body.position.dst2(safePosition) < safeRadius2) {
                false
            }
        }
        true
    }
    var tryCount = 0
    do {
        val repeat = tryPosition(safePosition.rnd(ctx.level.worldRadius))
        tryCount++
        if (tryCount > 10) {
            Gdx.app.error("Planet", "Try count reached 10, could not find safe position")
            return safePosition
        }
    } while (repeat)
    return safePosition
}

fun bullet(ctx: Context, pos: Vector2, vel: Vector2) {
    val e = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(pos)
                    linearVelocity.set(vel)
                    circle(radius = 0.1f) {
                        density = 2f
                        restitution = 1f
                        filter {
                            categoryBits = bulletCat
                            maskBits = all.xor(bulletCat).xor(rocketCat)
                        }
                    }
                }
        )
        bullet(1f)
        lifetime(5f)
    }
    ctx.engine.add(e)
}

fun planet(ctx: Context, radius: Float, pos: Vector2, omega: Float) {
    val e = entity {
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
    ctx.engine.add(e)
}

fun tank(ctx: Context, pos: Vector2) {
    val trunk = ctx.world.body(DynamicBody) {
        position.set(pos)
        gravityScale = -0.1f
        box(width = 2f, height = 0.2f) {
            density = 0.1f
            restitution = 0f
            filter {
                groupIndex = -1
            }
        }
    }

    val leftWheel = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(vec2(-1f, 0f) + pos)
                    circle(radius = 0.5f) {
                        density = 1f
                        restitution = 0f
                        friction = 1f
                        filter {
                            groupIndex = -1
                        }
                    }
                }
        )
    }
    ctx.engine.add(leftWheel)

    val rightWheel = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(vec2(1f, 0f) + pos)
                    circle(radius = 0.5f) {
                        density = 1f
                        restitution = 0f
                        friction = 1f
                        filter {
                            groupIndex = -1
                        }
                    }
                }
        )
    }
    ctx.engine.add(rightWheel)

    val turret = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(pos)
                    gravityScale = -0.1f
                    circle(radius = 0.5f) {
                        density = 1f
                        restitution = 0f
                        filter {
                            groupIndex = -1
                        }
                    }
                }
        )
    }
    ctx.engine.add(turret)

    val lufaMount = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(pos)
                    box(width = 0.1f, height = 0.1f) {
                        restitution = 0f
                        density = 1f
                        filter {
                            groupIndex = -1
                        }
                    }
                }
        )
    }
    ctx.engine.add(lufaMount)

    val lufa = entity {
        body(
                ctx.world.body(DynamicBody) {
                    position.set(vec2(0.5f, 0f) + pos)
                    gravityScale = -0.1f
                    box(width = 1f, height = 0.1f) {
                        restitution = 0f
                        density = 0.01f
                        filter {
                            groupIndex = -1
                        }
                    }
                }
        )
    }
    ctx.engine.add(lufaMount)

    val leftWheelJoint = trunk.wheelJointWith(leftWheel[body]) {
        frequencyHz = 8f
        dampingRatio = 0.9f
        maxMotorTorque = 8f
        localAxisA.set(0f, 1f)
        localAnchorA.set(-1f, 0f)
    }

    val leftWheelLimit = trunk.ropeJointWith(leftWheel[body]) {
        maxLength = 0.5f
        localAnchorA.set(-1f, 0f)
    }

    val rightWheelJoint = trunk.wheelJointWith(rightWheel[body]) {
        frequencyHz = 8f
        dampingRatio = 0.9f
        maxMotorTorque = 8f
        localAxisA.set(0f, 1f)
        localAnchorA.set(1f, 0f)
    }

    val rightWheelLimit = trunk.ropeJointWith(rightWheel[body]) {
        maxLength = 0.5f
        localAnchorA.set(1f, 0f)
    }

    val turretJoint = trunk.prismaticJointWith(turret[body]) {
        lowerTranslation = -0.5f
        upperTranslation = 0.5f
        enableLimit = true
        localAnchorA.set(0f, 0f)
        localAxisA.set(0f, 1f)
    }

    val turretSpring = trunk.distanceJointWith(turret[body]) {
        frequencyHz = 2f
        dampingRatio = 0.9f
        length = 0.5f
    }

    val lufaRevoluteJoint = turret[body].revoluteJointWith(lufaMount[body]) {
        enableLimit = true
        lowerAngle = -15.deg()
        upperAngle = 15.deg()
    }

    val lufaPrismaticJoint = lufaMount[body].prismaticJointWith(lufa[body]) {
        enableLimit = true
        lowerTranslation = -0.5f
        upperTranslation = 0.5f
        localAxisA.set(1f, 0f)
        enableMotor = true
        maxMotorForce = 10f
        motorSpeed = 1f
    }

    val tank = entity {
        body(trunk)
        cameraMagnet(1f)
        tank(leftWheelJoint, rightWheelJoint, lufa[body], lufaPrismaticJoint)
    }
    ctx.engine.add(tank)
}

fun edge(ctx: Context, from: Vector2, to: Vector2) {
    val e = entity {
        body(
                ctx.world.body {
                    edge(from, to) {
                        restitution = 0.1f
                        friction = 0.9f
                    }
                }
        )
    }

    ctx.engine.add(e)
}