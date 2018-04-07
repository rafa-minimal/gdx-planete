package com.minimal.arkanoid.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import ktx.box2d.body
import ktx.box2d.filter
import kotlin.experimental.or
import kotlin.experimental.xor

fun bullet(ctx: Context, pos: Vector2, velx: Float, vely: Float, catBits: Short, catMask: Short): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(DynamicBody) {
            position.set(pos)
            gravityScale = 0f
            linearVelocity.set(velx, vely)
            linearDamping = 0f
            fixedRotation = true
            box(0.2f, 0.5f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = catBits
                    maskBits = catMask
                }
            }
        })
        bullet(10f)
        sprite("laser-bullet")
    }
}

fun heroBullet(ctx: Context, pos: Vector2, velx: Float, vely: Float): MyEntity {
    return bullet(ctx, pos, velx, vely, cat.heroBullet, cat.all xor (cat.hero or cat.house or cat.heroBullet))
}

fun invaderBullet(ctx: Context, pos: Vector2, velx: Float, vely: Float): MyEntity {
    return bullet(ctx, pos, velx, vely, cat.invaderBullet, cat.all xor (cat.invader or cat.invaderBullet))
}