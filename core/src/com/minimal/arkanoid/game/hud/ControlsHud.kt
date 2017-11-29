package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.arkanoid.game.PlayerControl
import com.minimal.arkanoid.wrap.WrapCtx
import com.minimal.gdx.alphaButton
import com.minimal.gdx.pressed


class ControlsHud(stage: Stage, val player: PlayerControl) {
    val skin = WrapCtx.skin
    val root = Table(skin)
    val pause: Button
    val left: Image
    val right: Image
    val fire: Image

    //val pad = 5f

    init {
        val unit = unit()

        root.setFillParent(true)
        //root.pad(pad)

        pause = alphaButton(skin, "pause")

        val top = Table(skin)
        top.add().expandX()
        top.add(pause).size(unit)
        root.add(top).fillX().row()

        root.add().expand().row()
        stage.addActor(root)

        left = Image(skin, "left")
        left.setOrigin(unit/2f, unit/2f)
        right = Image(skin, "right")
        right.setOrigin(unit/2f, unit/2f)
        fire = Image(skin, "fire")
        fire.setOrigin(unit/2f, unit/2f)

        /*left.setBounds(pad, pad, unit, unit)
        right.setBounds(2*pad + unit, pad, unit, unit)
        fire.setBounds(Gdx.graphics.width - pad - unit, pad, unit, unit)*/

        left.setBounds(0f, 0f, unit, unit)
        right.setBounds(unit, 0f, unit, unit)
        fire.setBounds(Gdx.graphics.width - unit, 0f, unit, unit)

        stage.addActor(left)
        stage.addActor(right)
        stage.addActor(fire)
    }

    fun unit(): Float {
        var size = Gdx.graphics.width / 5f
        val sizeCm = size / Gdx.graphics.ppcX
        if (sizeCm > 3f) {
            size = 3f * Gdx.graphics.ppcX
        }
        return size
    }

    fun update() {
        val unit = unit()
        var leftPressed = false
        var rightPressed = false
        var firePressed = false

        for (pointer in 0 until 4) {
            if (Gdx.input.isTouched(pointer)) {
                val x = Gdx.input.getX(pointer)
                val y = Gdx.graphics.height - Gdx.input.getY(pointer)

                if (y < unit * 1.5f) {
                    if (x < unit) {
                        leftPressed = true
                    } else if (x < 2 * unit) {
                        rightPressed = true
                    } else if (x > Gdx.graphics.width - unit*1.5f) {
                        firePressed = true
                    }
                }
            }
        }

        var l = leftPressed || Keys.LEFT.pressed()
        var r = rightPressed || Keys.RIGHT.pressed()
        val f = firePressed || Keys.A.pressed()
        if (l && r) {
            l = false
            r = false
        }

        left.color.a = if (l) 0.6f else 0.3f
        right.color.a = if (r) 0.6f else 0.3f
        fire.color.a = if (f) 0.6f else 0.3f

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

    fun emphasisAction(): Action =
            Actions.forever(
                    Actions.sequence(
                            Actions.scaleTo(1.2f, 1.2f, 0.4f, Interpolation.sine),
                            Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine))
            )

    fun emphasizeLeft() {
        if (left.actions.size > 0) {
            return
        }
        left.addAction(
                emphasisAction()
        )
    }

    fun emphasizeRight() {
        if (right.actions.size > 0) {
            return
        }
        right.addAction(
                emphasisAction()
        )
    }

    fun emphasizeFire() {
        if (fire.actions.size > 0) {
            return
        }
        fire.addAction(
                emphasisAction()
        )
    }

    fun emphasizeEnd() {
        left.setScale(1f)
        right.setScale(1f)
        fire.setScale(1f)
        left.clearActions()
        right.clearActions()
        fire.clearActions()
    }
}