package com.minimal.utils

import com.badlogic.gdx.math.MathUtils

fun Float.rad_deg(): Float = this * MathUtils.radiansToDegrees
fun Float.deg_rad(): Float = this * MathUtils.degreesToRadians

fun Float.toRad(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Int.toRad(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Float.toDeg(): Float {
    return this / (2f * MathUtils.PI / 360f)
}

fun Int.toDeg(): Float {
    return this / (2f * MathUtils.PI / 360f)
}