package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.PlayerControl
import com.minimal.arkanoid.wrap.AlphaImageButton
import com.minimal.arkanoid.wrap.WrapCtx
import com.minimal.planet.pressed


class ControlsHud(val ctx: Context, val player: PlayerControl) {
    val left: AlphaImageButton
    val right: AlphaImageButton
    val fire: AlphaImageButton

    init {
        // 1/4 szerokości, ale max 2cm
        var size = Gdx.graphics.width / 4f
        val sizeCm = size / Gdx.graphics.ppcX
        if (sizeCm > 2f) {
            size = 2f * Gdx.graphics.ppcX
        }
        left = AlphaImageButton(WrapCtx.skin.getDrawable("arrow"))
        left.setPosition(0f, 0f)
        left.setSize(size, size)
        //left.scaleX = -1f
//        left.rotateBy(180f)
        ctx.stage.addActor(left)

        right = AlphaImageButton(WrapCtx.skin.getDrawable("arrow"))
        right.setPosition(size, 0f)
        right.setSize(size, size)
        ctx.stage.addActor(right)

        fire = AlphaImageButton(WrapCtx.skin.getDrawable("arrow"))
        fire.setPosition(Gdx.graphics.width - size, 0f)
        fire.setSize(size, size)
        ctx.stage.addActor(fire)
    }

    fun update() {
        var l = left.isPressed || Keys.LEFT.pressed()
        var r = right.isPressed || Keys.RIGHT.pressed()
        val f = fire.isPressed || Keys.A.pressed()
        if (l && r) {
            l = false
            r = false
        }

        player.left = l
        player.right = r
        if (f) {
            if (!player.fire) {
                player.fireJustPressed = true
            }
        } else {
            player.fireJustPressed = false
        }
        player.fire = f
    }
}