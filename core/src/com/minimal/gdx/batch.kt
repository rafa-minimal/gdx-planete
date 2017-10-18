package com.minimal.gdx

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch

fun SpriteBatch.render(camera: Camera, color: Color, body: () -> Unit) {
    this.projectionMatrix.set(camera.combined)
    this.begin()
    this.setColor(color)
    body()
    this.end()
}