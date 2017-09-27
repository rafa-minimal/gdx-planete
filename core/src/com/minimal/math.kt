package com.minimal

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2

object VecPool {
    val pool = Array<Vector2>(1000){ i -> Vector2(0f, 0f)}
    var firstFree = 0
    fun reset() {
        firstFree = 0
    }
    fun get(): Vector2 {
        if (pool.size > firstFree)
            return pool.get(firstFree++).setZero()
        else
            return Vector2()
    }
}


fun vec(): Vector2 {
    return VecPool.get().setZero()
}

fun vec(x: Float, y: Float): Vector2 {
    return VecPool.get().set(x, y)
}

fun vec(v: Vector2): Vector2 {
    return VecPool.get().set(v.x, v.y)
}

operator fun Vector2.unaryMinus(): Vector2 {
    return vec(-this.x, -this.y)
}

operator fun Vector2.plus(v2: Vector2): Vector2 = vec(this.x + v2.x, this.y + v2.y)

operator fun Vector2.minus(v2: Vector2): Vector2 = vec(this.x - v2.x, this.y - v2.y)

operator fun Vector2.times(v2: Vector2): Vector2 = vec(this.x * v2.x, this.y * v2.y)

operator fun Vector2.div(v2: Vector2): Vector2 = vec(this.x / v2.x, this.y / v2.y)

operator fun Vector2.times(s: Float): Vector2 = vec(this.x * s, this.y * s)

operator fun Vector2.div(s: Float): Vector2 = vec(this.x / s, this.y / s)

fun Vector2.norm(): Vector2 = vec(this.x / this.len(), this.y / this.len())

fun Vector2.boxang(): Float = Math.atan2(-this.x.toDouble(), this.y.toDouble()).toFloat()

operator fun Vector2.component1(): Float = this.x

operator fun Vector2.component2(): Float = this.y