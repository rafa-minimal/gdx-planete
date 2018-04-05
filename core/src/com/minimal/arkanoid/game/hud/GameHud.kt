package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.wrap.WrapCtx

class GameHud(stage: Stage, val ctx: Context) {
    val skin = WrapCtx.skin
    val root = Table(skin)
    var lastBallsCount = ctx.lives
    var lastTimeSec = 0
    val timeLabel = Label("", skin)
    val top: Table

    init {
        root.setFillParent(true)
        root.pad(5f)

        top = Table(skin)
        if (ctx.lives != -1) {
            repeat(lastBallsCount) {
                val image = Image(skin.getDrawable("circle"))
                image.color.set(Params.color_hud)
                top.add(image).size(20f)
            }
        }

        top.add().expandX()
        root.add(top).fillX().row()

        root.add().expand().row()

        timeLabel.setColor(Params.color_hud)
        val bottom = Table(skin)
        bottom.add(timeLabel)
        root.add(bottom).fillX().row()

        stage.addActor(root)
    }

    fun update() {
        if (ctx.lives != lastBallsCount) {
            lastBallsCount = ctx.lives
            top.clear()
            repeat(lastBallsCount) {
                top.add(Image(skin.getDrawable("circle"))).size(20f)
            }
            top.add().expandX()
        }

        if (ctx.levelTimeMs != -1) {
            val timeSec = (ctx.levelTimeMs - ctx.timeMs) / 1000
            if (timeSec != lastTimeSec) {
                val min = timeSec / 60
                val sec = timeSec - (min * 60)
                if (sec < 10)
                    timeLabel.setText(min.toString() + ":0" + sec)
                else
                    timeLabel.setText(min.toString() + ":" + sec)
                lastTimeSec = timeSec
            }
        }
    }
}