package com.minimal.gdx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

object VectorPool {
    var pool = Array<Vector2>(200){vec2()}
    var index = 0
    fun tvec(): Vector2 {
        return pool[index++].setZero()
    }
    fun tvec(x: Float, y: Float): Vector2 {
        return pool[index++].set(x, y)
    }
}

fun Vector2.rotateLeft(): Vector2 {
    return this.rotate90(1)
}

fun Vector2.rotateRight(): Vector2 {
    return this.rotate90(-1)
}

/**
 * Kąt według układu Box2d, czyli 0 - góra
 */
fun Vector2.boxAngleRad(): Float {
    return this.angleRad() - MathUtils.PI / 2f
}

fun Vector2.toPolar(): Vector2 {
    this.x = this.angleRad()
    this.y = this.len()
    return this
}

fun Vector2.toEuclid(): Vector2 {
    val angle = this.x
    val len = this.y
    val x = len * angle
    this.y = this.len()
    return this
}