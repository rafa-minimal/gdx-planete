package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint
import com.badlogic.gdx.physics.box2d.joints.WheelJoint
import com.minimal.ecs.ComponentTag
import ktx.math.plus
import ktx.math.vec2

class Energy(var total: Float,
             var energy: Float = total) {
    operator fun minusAssign(amount: Float) {
        energy = java.lang.Float.max(0f, energy - amount)
    }
}

class Bullet(val hitPoints: Float)

val fireInterval = 0.12f

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


class Lifetime(var lifetime: Float)

class Crash(val threshold: Float, val factor: Float)

val body = ComponentTag<Body>(0)
val energy = ComponentTag<Energy>(1)
val bullet = ComponentTag<Bullet>(2)
val tank = ComponentTag<TankControl>(3)
val lifetime = ComponentTag<Lifetime>(4)
val gravity = ComponentTag<Float>(5)
val crash = ComponentTag<Crash>(6)
val cameraMagnet = ComponentTag<Float>(7)
val asteroid = ComponentTag<Int>(8)