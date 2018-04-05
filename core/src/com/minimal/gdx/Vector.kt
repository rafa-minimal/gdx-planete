package com.minimal.gdx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
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

fun Vector3.set(vec: Vector2) {
    this.set(vec.x, vec.y, 0f)
}

fun Vector2.rnd(radius: Float): Vector2 {
    val rad = MathUtils.random(radius)
    val phi = MathUtils.random(MathUtils.PI2)
    return set(rad * MathUtils.cos(phi), rad * MathUtils.sin(phi))
}

fun Vector2.rotateLeft(): Vector2 {
    val x = this.x
    this.x = -y
    y = x
    return this
}

fun Vector2.rotateRight(): Vector2 {
    val x = this.x
    this.x = y
    y = -x
    return this
}

/**
 * Kąt według układu Box2d, czyli 0 - góra
 */
fun Vector2.boxAngleRad(): Float {
    return this.angleRad() - MathUtils.PI / 2f
}