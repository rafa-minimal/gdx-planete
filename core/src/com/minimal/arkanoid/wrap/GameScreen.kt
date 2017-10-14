package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.minimal.planet.ContextImpl

class GameScreen(val ctx: ContextImpl) : ScreenAdapter() {
    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ctx.engine.update(delta)
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