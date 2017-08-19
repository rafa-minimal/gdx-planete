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