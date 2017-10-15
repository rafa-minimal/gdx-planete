package com.minimal.arkanoid.game.fx

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.minimal.utils.FloatCircularBuffer
import ktx.math.vec2

class Tail(val tex: TextureRegion) {
    val maxPointsNumber = 60
    val fadeFactor = 0.8f
    val tailWidth = 1f
    val lastPoint = vec2()
    var hasLastPoint = false

    private val posX = FloatCircularBuffer(maxPointsNumber)
    private val posY = FloatCircularBuffer(maxPointsNumber)
    private val angle = FloatCircularBuffer(maxPointsNumber)
    private val length = FloatCircularBuffer(maxPointsNumber)
    private val alpha = FloatCircularBuffer(maxPointsNumber)

    private val tmp = vec2()

    fun add(pos: Vector2, alph: Float) {
        if (hasLastPoint) {
            tmp.set(pos).sub(lastPoint)
            val ang = tmp.angle()
            val len = tmp.len()
            tmp.rotate90(-1).scl(tailWidth/2f/len)
            posX.push(lastPoint.x+tmp.x)
            posY.push(lastPoint.y+tmp.y)
            alpha.push(alph)
            angle.push(ang)
            length.push(len)
        } else {
            hasLastPoint = true
        }
        lastPoint.set(pos)
    }

    fun breakTail() {
        hasLastPoint = false
    }

    fun update(timeStepSec: Float) {
        for (i in 0 until alpha.size()) {
            alpha[i] = alpha[i]*(1 - fadeFactor * timeStepSec)
        }
    }

    fun draw(batch: SpriteBatch) {
        for (i in 0 until alpha.size()) {
            batch.setColor(1f, 1f, 1f, alpha[i])
            batch.draw(tex, posX[i], posY[i],
                    0f, 0f,
                    length[i], tailWidth,
                    1f, 1f,
                    angle[i])
        }
    }
}