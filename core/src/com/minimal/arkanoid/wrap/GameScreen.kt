package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.minimal.arkanoid.game.ContextImpl
import com.minimal.planet.justPressed

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
        val refworldHeight = ctx.level.height
        val refworldWidth = ctx.level.width
        val xScale = refworldWidth  / width
        val yScale = refworldHeight  / height
        if (yScale < xScale) {
            ctx.worldCamera.viewportWidth = width * xScale
            ctx.worldCamera.viewportHeight = height * xScale
        } else {
            ctx.worldCamera.viewportWidth = width * yScale
            ctx.worldCamera.viewportHeight = height * yScale
        }

        ctx.worldCamera.position.set(ctx.level.width/2, ctx.level.height/2, 0f)

        ctx.worldCamera.update()
    }
}