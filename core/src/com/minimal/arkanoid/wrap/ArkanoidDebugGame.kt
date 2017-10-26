package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color

class ArkanoidDebugGame : ArkanoidGame() {
    override fun create() {
        super.create()
        gameScreen = GameScreen("1")
        setScreen(gameScreen!!)
        state = State.Game
    }

    override fun render() {
        super.render()
        with(WrapCtx) {
            batch.projectionMatrix.set(camera.combined)
            batch.begin()
            batch.setColor(Color.WHITE)
            font.draw(batch, "fps: ", 20f, 50f)
            font.draw(batch, Gdx.graphics.framesPerSecond.toString(), 50f, 50f)
            batch.end()
        }
    }
}