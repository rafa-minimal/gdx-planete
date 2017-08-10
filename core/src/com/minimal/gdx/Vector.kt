package com.minimal.gdx

import com.badlogic.gdx.math.Vector2
import ktx.collections.gdxArrayOf
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