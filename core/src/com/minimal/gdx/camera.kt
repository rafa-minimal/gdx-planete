package com.minimal.gdx

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

private val vec3 = Vector3()

fun OrthographicCamera.unproject(screenVec: Vector2): Vector2 {
    vec3.set(screenVec.x, screenVec.y, 0f)
    unproject(vec3)
    return screenVec.set(vec3.x, vec3.y)
}

fun OrthographicCamera.project(worldVec: Vector2): Vector2 {
    vec3.set(worldVec.x, worldVec.y, 0f)
    project(vec3)
    return worldVec.set(vec3.x, vec3.y)
}