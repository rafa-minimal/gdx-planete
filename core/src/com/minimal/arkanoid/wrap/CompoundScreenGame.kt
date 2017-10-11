package com.minimal.arkanoid.wrap

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Array

abstract class CompoundScreenGame : ApplicationListener {
    private val screens = Array<Screen>(5)

    override fun dispose() {
        hideScreens()
    }

    override fun pause() {
        for (screen in screens) {
            screen.pause()
        }
    }

    override fun resume() {
        for (screen in screens) {
            screen.resume()
        }
    }

    override fun render() {
        for (screen in screens) {
            screen.render(Gdx.graphics.deltaTime)
        }
    }

    override fun resize(width: Int, height: Int) {
        for (screen in screens) {
            screen.resize(width, height)
        }
    }

    fun setScreen(screen: Screen) {
        hideScreens()
        screens.clear()
        screens.add(screen)
        screen.show()
        screen.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    fun addScreen(screen: Screen) {
        screen.show()
        screen.resize(Gdx.graphics.width, Gdx.graphics.height)
        screens.add(screen)
    }

    private fun hideScreens() {
        for (screen in screens) {
            screen.hide()
        }
    }
}