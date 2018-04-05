package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Contact
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import ktx.box2d.body
import ktx.box2d.filter
import kotlin.experimental.or
import kotlin.experimental.xor


class Hero(val control: HeroControl) {

}

class HeroControlScript(val ctx: Context) : Script {
    override fun update(me: MyEntity, timeStepSec: Float) {
        if (me[hero].control.fireJustPressed) {
            bullet(ctx, me[body].position, 0f, Params.hero_bullet_velocity)
        }
        if (me[hero].control.jumpJustPressed) {
            me[body].applyLinearImpulse(0f, Params.hero_jump_impulse, 0f, 0f, true)
        }
        val dir = if (me[hero].control.left) -1 else 0 + if (me[hero].control.right) 1 else 0

        val vely = me[body].linearVelocity.y
        me[body].setLinearVelocity(dir * Params.hero_velocity, vely)
    }
}


fun createPlayer(ctx: Context, width: Float, playerY: Float, control: HeroControl): MyEntity {
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

    val hero = ctx.engine.entity {
        body(body)
        hero(control)
        //energy(5f)
        cameraMagnet(1f)
        //sprite("hero-body-1", true)
        script(HeroControlScript(ctx))
        texture(ctx.atlas.findRegion("circle"), 1f, 1f, scale = 0f, color = Params.color_ball)
    }
    return hero
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