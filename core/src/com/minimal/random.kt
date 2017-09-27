package com.minimal

import com.badlogic.gdx.math.MathUtils

fun rndBall(radius: Float) = vec(MathUtils.random(radius), 0f).rotate(MathUtils.random(360f))

fun rndBox(w: Float, h: Float) = vec(MathUtils.random(-w/2, w/2), MathUtils.random(-h/2, h/2))

fun rnd(from: Int, to: Int) = MathUtils.random(from, to)
