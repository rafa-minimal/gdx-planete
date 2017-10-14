package com.minimal.utils

import com.badlogic.gdx.math.MathUtils

fun Float.rad_deg(): Float = this * MathUtils.radiansToDegrees
fun Float.deg_rad(): Float = this * MathUtils.degreesToRadians