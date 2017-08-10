package com.minimal.planet

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.physics.box2d.Body
import com.minimal.ecs.ComponentTag

class Energy(var total: Float,
             var energy: Float = total) {
    operator fun minusAssign(amount: Float) {
        energy = java.lang.Float.max(0f, energy - amount)
    }
}

class Bullet(val hitPoints: Float)

val fireInterval = 0.12f

class RocketControl(val up: Int = Keys.UP,
                    val left: Int = Keys.LEFT,
                    val right: Int = Keys.RIGHT,
                    val fire: Int = Keys.A,
                    val angularTau: Float = 0.11f,
                    val maxAngularVel: Float = 4.5f) {
    var nextBullet: Float = fireInterval
}


class Lifetime(var lifetime: Float)

class Crash(val threshold: Float, val factor: Float)

val body = ComponentTag<Body>(0)
val energy = ComponentTag<Energy>(1)
val bullet = ComponentTag<Bullet>(2)
val rocket = ComponentTag<RocketControl>(3)
val lifetime = ComponentTag<Lifetime>(4)
val gravity = ComponentTag<Float>(5)
val crash = ComponentTag<Crash>(6)
val cameraMagnet = ComponentTag<Float>(7)
val asteroid = ComponentTag<Int>(8)