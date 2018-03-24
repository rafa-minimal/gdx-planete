package com.minimal.planet

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.minimal.ecs.ComponentTag
import com.minimal.planet.game.ents.Hero
import com.minimal.planet.game.systems.Sprite

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
val lifetime = ComponentTag<Lifetime>(4)
val gravity = ComponentTag<Float>(5)
val crash = ComponentTag<Crash>(6)
val cameraMagnet = ComponentTag<Float>(7)
val asteroid = ComponentTag<Int>(8)
val hero = ComponentTag<Hero>(9)
val sprite = ComponentTag<Sprite>(10)
val parent = ComponentTag<MyEntity>(11)
val position = ComponentTag<Vector2>(12)
