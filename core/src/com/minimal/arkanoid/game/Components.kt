package com.minimal.arkanoid.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.ecs.ComponentTag
import com.minimal.fx.SnakeTail
import ktx.collections.GdxArray

class Energy(var total: Float,
             var energy: Float = total) {
    operator fun minusAssign(amount: Float) {
        energy = Math.max(0f, energy - amount)
    }
}

class Bullet(val hitPoints: Float)

// Jeżeli jest kilka piłek w zasięgu, Player łapie piłkę o najniższym priorytecie
class Ball(var priority: Int)
object Box

class Lifetime(var lifetime: Float)

class Crash(val threshold: Float, val factor: Float)

class Parent(val parent: MyEntity)
class Children() {
    val children = GdxArray<MyEntity>()
    fun add(e: MyEntity) {
        children.add(e)
    }
}

class Texture(val texture: TextureRegion,
              val width: Float, val height: Float,
              val pos: Vector2, var angleDeg: Float = 0f,
              var scaleX: Float = 1f, var scaleY: Float = 1f,
              val color: Color = Color.WHITE)

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
val texture = ComponentTag<Texture>(13)
val parent = ComponentTag<Parent>(14)
val children = ComponentTag<Children>(15)
val tail = ComponentTag<SnakeTail>(16)