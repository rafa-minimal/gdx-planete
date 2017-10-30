package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.arkanoid.game.PlayerControl
import com.minimal.arkanoid.wrap.WrapCtx
import com.minimal.gdx.alphaButton
import com.minimal.gdx.pressed


class ControlsHud(val stage: Stage, val player: PlayerControl) {
    val skin = WrapCtx.skin
    val root = Table(skin)
    val pause: Button
    val left: Button
    val right: Button
    val fire: Button

    init {
        val unit = unit()

        root.setFillParent(true)
        root.pad(5f)

        pause = alphaButton(skin, "pause")
        left = alphaButton(skin, "left")
        right = alphaButton(skin, "right")
        fire = alphaButton(skin, "fire")

        val top = Table(skin)
        top.add().expandX()
        top.add(pause).size(unit)
        root.add(top).fillX().row()

        root.add().expand().row()

        val bottom = Table(skin)
        bottom.defaults().bottom()
        bottom.add(left).size(unit)
        bottom.add(right).size(unit)
        bottom.add().expandX()
        bottom.add(fire).size(unit)
        root.add(bottom).fillX().row()

        stage.addActor(root)
    }

    fun unit(): Float {
        var size = Gdx.graphics.width / 5f
        val sizeCm = size / Gdx.graphics.ppcX
        if (sizeCm > 2f) {
            size = 2f * Gdx.graphics.ppcX
        }
        return size
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