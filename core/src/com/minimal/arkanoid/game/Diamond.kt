package com.minimal.arkanoid.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.minimal.arkanoid.game.entity.entity
import ktx.box2d.body
import ktx.box2d.filter



private val diamondVertices = floatArrayOf(-2f, 0f, 0f, -2f, 2f, 0f, 1f, 1f, -1f, 1f)
private val diamondVerticesScaled = FloatArray(diamondVertices.size) { i -> diamondVertices[i] * 0.4f }
fun createDiamond(ctx: Context, pos: Vector2) {
    ctx.engine.entity {
        body(ctx.world.body(DynamicBody) {
            linearDamping = 0.4f
            angularDamping = 0.4f
            position.set(pos)
            polygon(diamondVerticesScaled) {
                density = 0.2f
                restitution = 0.1f
                filter {
                    categoryBits = default
                }
            }
        })
        powerUp(Diamond)
    }
}