package com.minimal.arkanoid.game.system

import com.badlogic.gdx.Gdx
import com.minimal.ecs.System
import com.minimal.arkanoid.game.Context
import com.minimal.planet.justPressed
import com.minimal.arkanoid.game.player
import com.minimal.planet.pressed

class PlayerControlUpdateSystem(val ctx: Context) : System {
    val family = ctx.engine.family(player)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, player ->
            val wasFire = player.fire
            player.fire = false
            player.left = false
            player.right = false

            for (i in 0..4) {
                if (Gdx.input.isTouched(i)) {
                    var x = Gdx.input.getX(i)
                    val y = Gdx.graphics.height - Gdx.input.getY(i)
                    if (x < Gdx.graphics.width / 2) {
                        if (y > x)
                            player.left = true

                        else
                            player.right = true
                    }
                    x = Gdx.graphics.width - Gdx.input.getX(i)
                    if (x < Gdx.graphics.width / 2) {
                        player.fire = true
                        player.fireJustPressed = !wasFire
                    }
                }
            }
            player.fire = player.fire or player.fireKey.pressed()
            player.fireJustPressed = player.fireJustPressed or player.fireKey.justPressed()
            player.left = player.left or player.leftKey.pressed()
            player.right = player.right or player.rightKey.pressed()
        }
    }
}