package com.minimal.arkanoid.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.filter
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor


class Hero(val control: HeroControl) {

}

class HeroControlScript(val ctx: Context, val sensor: Fixture) : Script {
    var nextJumpMs = 0
    var nextFireMs = 0
    var bodiesUnderFeet = 0

    fun isGrounded() = bodiesUnderFeet > 0

    override fun update(me: MyEntity, timeStepSec: Float) {
        if (me[hero].control.fire && ctx.timeMs > nextFireMs) {
            heroBullet(ctx, me[body].position, 0f, Params.hero_bullet_velocity)
            nextFireMs = ctx.timeMs + Params.fire_delay_ms
        }
        if (me[hero].control.jump && isGrounded() && ctx.timeMs > nextJumpMs) {
            me[body].applyLinearImpulse(0f, Params.hero_jump_impulse, 0f, 0f, true)
            nextJumpMs = ctx.timeMs + Params.jump_delay_ms
        }
        val dir = if (me[hero].control.left) -1 else 0 + if (me[hero].control.right) 1 else 0

        val vely = me[body].linearVelocity.y
        me[body].setLinearVelocity(dir * Params.hero_velocity, vely)
    }

    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (contact.fixtureA == sensor || contact.fixtureB == sensor) {
            bodiesUnderFeet++
        }
    }

    override fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (contact.fixtureA == sensor || contact.fixtureB == sensor) {
            bodiesUnderFeet--
        }
    }

    override fun beforeDestroy(me: MyEntity) {
        Actions.schedule(2f) {
            if (ctx.takeLive())
                createHero(ctx, ctx.level.width, 1.5f, me[hero].control)
        }
    }
}


fun createHero(ctx: Context, width: Float, playerY: Float, control: HeroControl): MyEntity {
    val body = ctx.world.body(DynamicBody) {
        position.set(width / 2f, playerY)
        gravityScale = 10f
        fixedRotation = true
        linearDamping = Params.heroStopDamping
        circle(radius = 0.4f) {
            density = 1f
            restitution = 0f
            friction = 0f
            filter {
                categoryBits = cat.hero
                maskBits = cat.all xor (cat.hero or cat.house or cat.heroBullet)
            }
        }
    }

    val sensor = body.box(0.8f, 0.2f, Vector2(0f, -0.4f)) {
        isSensor = true
        density = 0f
        filter {
            categoryBits = cat.default
            maskBits = cat.default
        }
    }

    val hero = ctx.engine.entity {
        body(body)
        hero(control)
        //energy(5f)
        cameraMagnet(1f)
        //sprite("hero-body-1", true)
        script(HeroControlScript(ctx, sensor))
        //texture(ctx.atlas.findRegion("circle"), 1f, 1f, scale = 0f, color = Params.color_ball)
        sprite("hero-body-1")
        script(DieOnContact(cat.invader))
    }
    return hero
}

class DieOnContact(val mask: Short) : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        for (fixture in other[body].fixtureList) {
            if (fixture.filterData.categoryBits and mask != 0.toShort()) {
                me.dead = true
            }
        }
    }
}

class PowerUpCollector(val ctx: Context) : Script {
    override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
        if (other.contains(pup)) {
            when (other.get(pup)) {
                Diamond -> {
                    other.dead = true
                }
            }
        }
    }
}