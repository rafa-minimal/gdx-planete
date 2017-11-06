package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.wrap.WrapCtx

class GameHud(val stage: Stage, val ctx: Context) {
    val skin = WrapCtx.skin
    val root = Table(skin)
    var lastBallsCount = ctx.balls
    val top: Table

    init {
        root.setFillParent(true)
        root.pad(5f)

        top = Table(skin)
        repeat(lastBallsCount) {
            top.add(Image(skin.getDrawable("circle"))).size(20f)
        }
        top.add().expandX()
        root.add(top).fillX().row()

        root.add().expand().row()

        val bottom = Table(skin)
        bottom.add("10:00", "default")
        root.add(bottom).fillX().row()

        stage.addActor(root)
    }

    fun update() {
        if (ctx.balls != lastBallsCount) {
            lastBallsCount = ctx.balls
            top.clear()
            repeat(lastBallsCount) {
                top.add(Image(skin.getDrawable("circle"))).size(20f)
            }
            top.add().expandX()
        }
    }
}