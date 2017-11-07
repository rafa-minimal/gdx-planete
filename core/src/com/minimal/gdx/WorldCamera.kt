package com.minimal.gdx

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

/**
 * Makes sure the world fits in the screen
 */
class WorldCamera(var worldWidth: Float, var worldHeight: Float) : OrthographicCamera() {

    private val worldCenter = vec2()

    fun setWorldCenter(center: Vector2) {
        worldCenter.set(center)
        update()
    }

    fun setSize(w: Float, h: Float) {
        worldWidth = w
        worldHeight = h
        update()
    }

    fun resize(width: Int, height: Int) {
        val xScale = worldWidth  / width
        val yScale = worldHeight  / height
        if (yScale < xScale) {
            viewportWidth = width * xScale
            viewportHeight = height * xScale
        } else {
            viewportWidth = width * yScale
            viewportHeight = height * yScale
        }

        position.set(worldCenter.x, worldCenter.y, 0f)

        update()
    }
}