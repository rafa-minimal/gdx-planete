package com.minimal.arkanoid.game.entity

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.script.BulletScript
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.script.CrashScript
import com.minimal.ecs.Entity
import com.minimal.planet.*

class MyEntity : Entity() {
    val scripts = mutableListOf<Script>()
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
    fun player(rangeFixture: Fixture) {
        e.add(player, Player(rangeFixture))
    }

    fun script(script: Script) {
        e.add(script)
    }

    fun ball() {
        e.add(ball, Ball)
    }

    fun powerUp(powerUp: PowerUp) {
        e.add(pup, powerUp)
    }
}

fun MyEngine.entity(init: EntityBuilder.() -> Unit): MyEntity {
    val builder = EntityBuilder()
    builder.init()
    val e = builder.build()
    this.add(e)
    return e
}