package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.hud.ControlsHud
import com.minimal.arkanoid.game.level.Level
import com.minimal.arkanoid.game.level.LevelResult
import com.minimal.gdx.render
import com.minimal.planet.justPressed
import ktx.math.vec2

class GameScreen(var ctx: Context) : ScreenAdapter() {

    private var running = true
    val bg = Texture("bg2.png")

    lateinit var hud: ControlsHud

    override fun show() {
        hud = ControlsHud(ctx, ctx.playerControl)
        WrapCtx.mux.addProcessor(ctx.stage)
    }

    override fun hide() {
        WrapCtx.mux.removeProcessor(ctx.stage)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        hud.update()

        ctx.batch.render(WrapCtx.camera, Color.WHITE) {
            ctx.batch.draw(bg, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }

        if(Keys.R.justPressed()) {
            ctx.dispose()
            ctx = Context(Level())
            resize(Gdx.graphics.width, Gdx.graphics.height)
        }
        if(Keys.P.justPressed()) {
            ctx.worldCamera.zoom *= 2f
            ctx.worldCamera.update()
        }
        if(Keys.O.justPressed()) {
            ctx.worldCamera.zoom /= 2f
            ctx.worldCamera.update()
        }
        if(Keys.K.justPressed()) {
            ctx.cameraSystem.shake(vec2(0.5f, 0f))
        }
        var step = 0f
        if(running || Keys.S.justPressed()) {
            step = delta
        }
        if(Keys.SPACE.justPressed()) {
            running = !running
        }
        ctx.engine.update(step)
    }

    override fun resize(width: Int, height: Int) {
        ctx.cameraSystem.resize(width, height)
        ctx.viewport.setWorldSize(width.toFloat(), height.toFloat())
    }

    fun result(): LevelResult {
        return ctx.level.result()
    }
}