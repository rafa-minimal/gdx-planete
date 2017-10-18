package com.minimal.arkanoid.game.system

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.minimal.ecs.System
import com.minimal.gdx.set
import ktx.math.vec2

class CameraSystem(val camera: Camera, val worldWidth: Float, val worldHeight: Float) : System {
    // Gdzie celuje oś kamery w układzie świata
    val worldPosition = vec2()

    val deltaPos = vec2()
    val vel = vec2()

    val drag = 10f
    val spring = 100f

    val tmp = vec2()

    override fun update(dt: Float) {
        vel.x -= vel.x * drag * dt + deltaPos.x * spring * dt
        vel.y -= vel.y * drag * dt + deltaPos.y * spring * dt

        deltaPos.x += vel.x * dt
        deltaPos.y += vel.y * dt

        tmp.set(worldPosition).add(deltaPos)
        camera.position.set(tmp)
        camera.update()
    }

    fun resize(width: Int, height: Int) {
        val xScale = worldWidth  / width
        val yScale = worldHeight  / height
        if (yScale < xScale) {
            camera.viewportWidth = width * xScale
            camera.viewportHeight = height * xScale
        } else {
            camera.viewportWidth = width * yScale
            camera.viewportHeight = height * yScale
        }

        tmp.set(worldPosition).add(deltaPos)
        camera.position.set(tmp)

        camera.update()
    }

    fun shake(delta: Vector2) {
        deltaPos.set(delta)
    }
}