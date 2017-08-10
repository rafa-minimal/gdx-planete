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

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if(Keys.ESCAPE.pressed()) {
            Gdx.app.exit()
        }

        ctx.engine.update(Gdx.graphics.rawDeltaTime)
    }

    override fun dispose() {
        ctx.dispose()
    }

    override fun resize(width: Int, height: Int) {
        val refworldRadius = ctx.level.worldRadius
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