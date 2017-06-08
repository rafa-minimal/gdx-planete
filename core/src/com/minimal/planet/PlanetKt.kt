package com.minimal.planet

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class PlanetKt : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    lateinit var img: Texture
    var pos = 0f

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        pos += 1
        pos = if (pos > Gdx.graphics.width) 0f else pos;

        batch.begin()
        batch.draw(img, pos, 0f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}