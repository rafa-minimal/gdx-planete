package com.minimal.planet

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.GL20

class Planet : ApplicationAdapter() {
    lateinit var ctx: ContextImpl

    override fun create() {
        ctx = ContextImpl()
    }

    private var running = true

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if(Keys.ESCAPE.pressed()) {
            Gdx.app.exit()
        }
        if(Keys.R.justPressed()) {
            ctx.dispose()
            ctx = ContextImpl()
            resize(Gdx.graphics.width, Gdx.graphics.height)
        }
        var step = 0f
        if(running || Keys.S.justPressed()) {
            step = Gdx.graphics.rawDeltaTime
        }
        if(Keys.SPACE.justPressed()) {
            running = !running
        }
        if(Keys.P.justPressed()) {
            ctx.worldCamera.zoom *= 2f
            ctx.worldCamera.update()
        }
        if(Keys.O.justPressed()) {
            ctx.worldCamera.zoom /= 2f
            ctx.worldCamera.update()
        }
        ctx.engine.update(step)
    }

    override fun dispose() {
        ctx.dispose()
    }

    override fun resize(width: Int, height: Int) {
        ctx.worldCamera.position.y = ctx.level.worldRadius

        val refworldRadius = ctx.level.worldRadius/2
        val xScale = refworldRadius * 2f / width
        val yScale = refworldRadius * 2f / height
        if (xScale < yScale) {
            ctx.worldCamera.viewportWidth = width * xScale
            ctx.worldCamera.viewportHeight = height * xScale
        } else {
            ctx.worldCamera.viewportWidth = width * yScale
            ctx.worldCamera.viewportHeight = height * yScale
        }
        ctx.worldCamera.update()
    }
}