package com.minimal.gdx

import com.badlogic.gdx.Gdx

fun Int.pressed(): Boolean {
    return Gdx.input.isKeyPressed(this)
}

fun Int.justPressed(): Boolean {
    return Gdx.input.isKeyJustPressed(this)
}