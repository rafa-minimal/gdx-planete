package com.minimal.arkanoid.game

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.wrap.WrapCtx
import ktx.box2d.body
import ktx.box2d.filter

fun createBox(ctx: Context, x: Float, y: Float): MyEntity {
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
        energy(40f)
        script(BoxScript)
        sprite("box-4")
    }
}

object BoxScript  : Script {
    val textures = List(4) {i -> WrapCtx.gameAtlas.findRegion("box-" + (i+1))}

    override fun update(me: MyEntity, timeStepSec: Float) {
        val texIndex = MathUtils.clamp(me[energy].energy.toInt() / 10 - 1, 0, textures.lastIndex)
        me[sprite].textureRegion = textures[texIndex]
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
        sprite("floor")
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
        energy(30f)
        house()
        sprite("house-3")
        script(DieOnContact(cat.invader))
        script(HouseScript)
    }
}

object HouseScript : Script {
    val textures = List(3) {i -> WrapCtx.gameAtlas.findRegion("house-" + (i+1))}

    override fun update(me: MyEntity, timeStepSec: Float) {
        val texIndex = MathUtils.clamp(me[energy].energy.toInt() / 10 - 1, 0, textures.lastIndex)
        me[sprite].textureRegion = textures[texIndex]
    }
}