package com.minimal.arkanoid.game.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.script.BulletScript
import com.minimal.arkanoid.game.script.CrashScript
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.system.Sprite
import com.minimal.arkanoid.wrap.WrapCtx
import com.minimal.ecs.Entity
import com.minimal.fx.SnakeTail

class MyEntity : Entity() {
    val scripts = mutableListOf<Script>()
    val scriptsToRemove = mutableListOf<Script>()
    val scriptsToAdd = mutableListOf<Script>()
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

    fun script(script: Script) {
        e.add(script)
    }

    fun ball(priority: Int) {
        e.add(ball, Ball(priority))
    }

    fun powerUp(powerUp: PowerUp) {
        e.add(pup, powerUp)
    }

    fun hero(control: HeroControl) {
        e.add(hero, Hero(control))
    }

    fun box() {
        e.add(box, Box)
    }

    fun texture(tex: TextureRegion,
                width: Float, height: Float,
                pos: Vector2 = Vector2(), angle: Float = 0f,
                scale: Float = 1f, color: Color = Color.WHITE) {
        e.add(texture, Texture(tex, width, height, pos, angle, scale, scale, color))
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
    fun sprite(texName: String, faceUp: Boolean = false) {
        /*val t = wrap().atlas.findRegion(texName) ?: throw IllegalStateException("Texture not found in game atlas: " + texName)
        val s = Sprite(t)
        s.setOrigin(t.regionWidth / 2f, t.regionHeight / 2f)
        s.setSize(size, size)
        e.add(sprite, s)*/
        e.add(sprite, Sprite(WrapCtx.gameAtlas.findRegion(texName), faceUp))
    }
    fun sprite(texName: String, init: Sprite.() -> Unit) {
        val s = Sprite(WrapCtx.gameAtlas.findRegion(texName))
        s.init()
        e.add(sprite, s)
    }
}

fun MyEngine.entity(init: EntityBuilder.() -> Unit): MyEntity {
    val builder = EntityBuilder()
    builder.init()
    val e = builder.build()
    this.add(e)
    return e
}