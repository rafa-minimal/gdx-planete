package com.minimal.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import kotlin.experimental.and

private object categoryCheck : QueryCallback {
    var categoryMask: Short = 0
    var result: Boolean = false

    override fun reportFixture(fixture: Fixture?): Boolean {
        if (fixture!!.filterData.categoryBits and categoryMask != 0.toShort()) {
            result = true
            return false    // terminate
        }
        return true
    }
}

fun World.querySquare(pos: Vector2, size: Float, categotyMask: Short): Boolean {
    categoryCheck.categoryMask = categotyMask
    categoryCheck.result = false
    this.QueryAABB(categoryCheck, pos.x - size / 2, pos.y - size / 2, pos.x + size / 2, pos.y + size / 2)
    return categoryCheck.result
}

fun Body.tangentVelocity(): Float =
        this.position.nor().rotate90(-1).times(this.linearVelocity).len()

fun Body.tangentVector(): Vector2 =
        this.position.nor().rotate90(-1)

/*
fun filter(init: Filter.() -> Unit): Filter {
    val filter = Filter()
    filter.init()
    return filter
}

var i = 0
fun next() = 1.shl(i++).toShort()
val rocketCat = next()
val bulletCat = next()
val asteroidCat = next()

val bulletFilter = filter{
    categoryBits = bulletCat
    maskBits = -1.and()
}*/
