package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.Body
import com.minimal.ecs.ComponentTag

class Energy(var total: Float,
             var energy: Float = total) {
    operator fun minusAssign(amount: Float) {
        energy = Math.max(0f, energy - amount)
    }
}

class Bullet(val hitPoints: Float)

object Ball
object Box

class Lifetime(var lifetime: Float)

class Crash(val threshold: Float, val factor: Float)

open class PowerUp
object Diamond : PowerUp()
object ExtraTime : PowerUp()

val body = ComponentTag<Body>(0)
val energy = ComponentTag<Energy>(1)
val bullet = ComponentTag<Bullet>(2)
val lifetime = ComponentTag<Lifetime>(4)
val gravity = ComponentTag<Float>(5)
val crash = ComponentTag<Crash>(6)
val cameraMagnet = ComponentTag<Float>(7)
val box = ComponentTag<Box>(8)
val player = ComponentTag<Player>(10)
val ball = ComponentTag<Ball>(11)
val pup = ComponentTag<PowerUp>(12)