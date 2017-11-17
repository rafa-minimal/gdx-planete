package com.minimal.arkanoid.game.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.script.BulletScript
import com.minimal.arkanoid.game.script.CrashScript
import com.minimal.arkanoid.game.script.Script
import com.minimal.ecs.Entity
import com.minimal.fx.SnakeTail

class MyEntity : Entity() {
    val scripts = mutableListOf<Script>()
    val scriptsToRemove = mutableListOf<Script>()
    fun add(s: Script) {
        scripts.add(s)
    }
}

class EntityBuilder() {
    val e = MyEntity()
    fun build(): MyEntity {
        return e
    }
    fun body(b: Body) {
        e.add(body, b)
        b.userData = e
    }
    fun energy(total: Float) {
        e.add(energy, Energy(total))
    }
    fun bullet(hitPoints: Float) {
        e.add(bullet, Bullet(hitPoints))
        e.add(BulletScript)
    }
    fun lifetime(lifeSec: Float) {
        e.add(lifetime, Lifetime(lifeSec))
    }
    fun gravity(value: Float) {
        e.add(gravity, value)
    }
    fun crash(threshold: Float = 1f, factor: Float = 1f) {
        e.add(crash, Crash(threshold, factor))
        e.add(CrashScript)
    }
    fun cameraMagnet(magnet: Float) {
        e.add(cameraMagnet, magnet)
    }
    fun player(rangeFixture: Fixture, playerControl: PlayerControl) {
        e.add(player, Player(rangeFixture, playerControl))
    }

    fun script(script: Script) {
        e.add(script)
    }

    fun ball(priority: Int) {
        e.add(ball, Ball(priority))
    }

    fun powerUp(powerUp: PowerUp) {
        e.add(pup, powerUp)
    }

    fun box() {
        e.add(box, Box)
    }

    fun texture(tex: TextureRegion, width: Float, height: Float, pos: Vector2 = Vector2(), color: Color = Color.WHITE) {
        e.add(texture, Texture(tex, width, height, pos, 1f, 1f, color))
    }
    fun parent(entity: MyEntity) {
        e.add(parent, Parent(entity))
        if (!entity.contains(children)) {
            entity.add(children, Children())
        }
        entity.get(children).add(e)
    }
    fun tail(t: SnakeTail) {
        e.add(tail, t)
    }
}

fun MyEngine.entity(init: EntityBuilder.() -> Unit): MyEntity {
    val builder = EntityBuilder()
    builder.init()
    val e = builder.build()
    this.add(e)
    return e
}