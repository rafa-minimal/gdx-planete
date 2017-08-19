package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint
import com.badlogic.gdx.physics.box2d.joints.WheelJoint
import com.minimal.ecs.System
import ktx.box2d.*
import ktx.math.plus
import ktx.math.vec2


class TankControl(val up: Int = Keys.UP,
                  val left: Int = Keys.LEFT,
                  val right: Int = Keys.RIGHT,
                  val fire: Int = Keys.A,
                  val leftWheel: WheelJoint,
                  val rightWheel: WheelJoint,
                  val lufa: Body,
                  val lufaPrismaticJoint: PrismaticJoint) {
    var nextBullet: Float = fireInterval
    val lufaEnd = vec2(0.5f, 0f)
    val bulletStart = vec2(20f, 0f)
    val shotForce = vec2(-11f, 0f)
    var side = 1
    fun bulletStartPos(): Vector2 =
            lufa.getWorldPoint(lufaEnd)
    fun bulletStartVel(): Vector2 =
            //lufa.getWorldVector(bulletStart) + lufa.getLinearVelocityFromLocalPoint(bulletStart)
            lufa.getWorldVector(bulletStart) + lufa.getLinearVelocity()
    fun shotForce(): Vector2 =
            lufa.getWorldVector(shotForce)
    fun flip() {
        lufaPrismaticJoint.motorSpeed = -lufaPrismaticJoint.motorSpeed
        lufaEnd.scl(-1f)
        bulletStart.scl(-1f)
        shotForce.scl(-1f)
        side *= -1
    }
}

class TankControlSystem(val ctx: Context) : System {
    val family = ctx.engine.family(tank, body)
    override fun update(timeStepSec: Float) {
        family.foreach {
            tank, body ->
            val left = tank.left.pressed()
            val right = tank.right.pressed()
            /*if (tank.up.pressed()) {
                body.applyForceToCenter(body.getWorldVector(vec2(0f, 10f)), true)
            }*/
            if (left && !right) {
                if(tank.side == 1) {
                    tank.flip()
                }
                tank.leftWheel.motorSpeed = 20f
                tank.rightWheel.motorSpeed = 20f
                tank.leftWheel.enableMotor(true)
                tank.rightWheel.enableMotor(true)
            } else if (!left && right) {
                if(tank.side == -1) {
                    tank.flip()
                }
                tank.leftWheel.motorSpeed = -20f
                tank.rightWheel.motorSpeed = -20f
                tank.leftWheel.enableMotor(true)
                tank.rightWheel.enableMotor(true)
            } else {
                tank.leftWheel.enableMotor(false)
                tank.rightWheel.enableMotor(false)
            }
            tank.nextBullet -= timeStepSec
            if (tank.fire.pressed() && tank.nextBullet <= 0f) {
                tank.nextBullet = fireInterval
                bullet(ctx,
                        pos = tank.bulletStartPos(),
                        vel = tank.bulletStartVel())
                tank.lufa.applyForceToCenter(tank.shotForce(), true)
            }
        }
    }
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