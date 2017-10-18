package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.minimal.arkanoid.game.ContextImpl
import com.minimal.arkanoid.game.level.LevelResult
import com.minimal.planet.justPressed
import ktx.math.vec2

class GameScreen(var ctx: ContextImpl) : ScreenAdapter() {

    private var running = true

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Keys.R.justPressed()) {
            ctx.dispose()
            ctx = ContextImpl()
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
    }

    fun result(): LevelResult {
        return ctx.level.result()
    }
}