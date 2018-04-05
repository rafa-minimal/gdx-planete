package com.minimal.arkanoid.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import ktx.box2d.body
import ktx.box2d.filter
import kotlin.experimental.or

fun box(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = cat.default
                }
            }
        })
        energy(10f)
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
    }
}

fun floor(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = cat.default
                }
            }
        })
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
    }
}

fun house(ctx: Context, x: Float, y: Float): MyEntity {
    return ctx.engine.entity {
        body(ctx.world.body(StaticBody) {
            position.set(x, y)
            box(1f, 1f) {
                density = 1f
                restitution = 0f
                filter {
                    categoryBits = cat.house
                }
            }
        })
        energy(10f)
        texture(ctx.atlas.findRegion("box"), 1f, 1f, color = Params.color_box)
        box() // house - żeby zliczać, czy wszystkie stoją
        script(DieOnContact(cat.invader or cat.invaderBullet))
    }
}