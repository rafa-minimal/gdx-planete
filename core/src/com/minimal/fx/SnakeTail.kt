package com.minimal.fx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.minimal.gdx.rotateRight
import com.minimal.utils.FloatArray3d
import com.minimal.utils.FloatCircularBuffer
import ktx.math.vec2

class SnakeTail(val tex: TextureRegion, val tailWidth: Float, SEGMENTS: Int, color: Color) {
    val lastPoint = vec2()
    var hasLastPoint = false

    val VERTS_PER_SEGMENT = 4
    val POINTS_PER_VERT = 5
    val colorFloat = color.toFloatBits()

    val u = tex.u
    val v = tex.v2
    val u2 = tex.u2
    val v2 = tex.v

    private val vertices = FloatArray3d(SEGMENTS, VERTS_PER_SEGMENT, POINTS_PER_VERT)
    //  2 - for segment end point, 2 for normal vector
    private val points = FloatCircularBuffer(SEGMENTS * 4)

    private val tmp = vec2()

    init {
        val texX = arrayOf(u, u, u2, u2)
        val texY = arrayOf(v, v2, v2, v)
        for (i in 0 until SEGMENTS) {
            for (j in 0 .. 3) {
                vertices[i, j, 2] = colorFloat
                vertices[i, j, 3] = texX[j]
                vertices[i, j, 4] = texY[j]
            }
        }
    }

    fun add(pos: Vector2) {
        points.push(pos.x)
        points.push(pos.y)
        if (!hasLastPoint) {
            lastPoint.set(pos)
        }
        tmp.set(pos).sub(lastPoint).nor().rotateRight()
        points.push(tmp.x)
        points.push(tmp.y)

        hasLastPoint = true
        lastPoint.set(pos)
    }

    val left = vec2()
    val right = vec2()
    val point = vec2()
    val normal = vec2()
    fun draw(batch: SpriteBatch) {
        val sc = points.size()/4        // segments count

        point.set(points[0], points[1])
        left.set(point)
        right.set(point)
        for (i in 1 until sc) {
            val si = i - 1
            vertices[si, 0, 0] = right.x
            vertices[si, 0, 1] = right.y
            vertices[si, 1, 0] = left.x
            vertices[si, 1, 1] = left.y

            point.set(points[i*4], points[i*4+1])
            normal.set(points[i*4+2], points[i*4+3])
            val w = tailWidth * i / sc
            normal.scl(w)
            right.set(point).add(normal)
            left.set(point).sub(normal)

            vertices[si, 2, 0] = left.x
            vertices[si, 2, 1] = left.y
            vertices[si, 3, 0] = right.x
            vertices[si, 3, 1] = right.y
        }

        batch.draw(tex.texture, vertices.array, 0, sc * VERTS_PER_SEGMENT * POINTS_PER_VERT)
    }
}