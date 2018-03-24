package com.minimal.planet.game.systems

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body
import com.minimal.ecs.System
import com.minimal.gdx.boxAngleRad
import com.minimal.planet.*
import com.minimal.planet.wrap.wrap

class Sprite(
        val textureRegion: TextureRegion,
        var width: Float = 0f,
        var height: Float = 0f,
        var angleRad: Float = 0f,
        var offsetX: Float = 0f,
        var offsetY: Float = 0f,
        var rotOrigX: Float = 0f,
        var rotOrigY: Float = 0f,
        var faceUp: Boolean = false
) {
    var scaleX: Float = 1f
    var scaleY: Float = 1f

    constructor(tex: String, faceUp: Boolean = false) : this(wrap().atlas.findRegion(tex), 0f, 0f, 0f, 0f, 0f, 0f, 0f, faceUp) {
        // przy założeniu, że tekstury generujemy w takiej wielkości, żeby 96 piksli przypadało na metr
        width = textureRegion.regionWidth / 96f
        height = textureRegion.regionHeight / 96f
    }
}

class SpriteSystem : System {
    override fun update(timeStepSec: Float) {
        wrap().batch.setProjectionMatrix(ctx().worldCamera.combined)
        wrap().batch.begin()
        ctx().engine.family(body, sprite).foreach { body, sprite ->
            draw(body, sprite)
        }
        ctx().engine.family(parent, sprite).foreach { parent, sprite ->
            if (parent.contains(body)) {
                draw(parent[body], sprite)
            } else {
                throw NotImplementedError("Parent nie ma body, nie zaimplementowałem przechodzenia do kolejnych parentów")
            }
        }
        wrap().batch.end()
    }

    private fun draw(body: Body, sprite: Sprite) {
        val localUpAngle = if (sprite.faceUp) {
            body.position.boxAngleRad().toDeg()
        } else {
            0f
        }

        wrap().batch.draw(sprite.textureRegion,
                body.position.x + sprite.offsetX - sprite.width / 2,
                body.position.y + sprite.offsetY - sprite.height / 2,
                sprite.rotOrigX + sprite.width / 2,
                sprite.rotOrigY + sprite.height / 2,
                sprite.width, sprite.height,
                sprite.scaleX, sprite.scaleY,
                localUpAngle + sprite.angleRad.toDeg())

        /*sprite.setPosition(body.position.x - sprite.originX, body.position.y - sprite.originY)
        sprite.draw(wrap().batch)*/
    }
}