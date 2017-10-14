package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.minimal.planet.ContextImpl

class ArkanoidGame : CompoundScreenGame() {

    enum class State {
        WelcomeScreen,
        Menu,
        Game
    }

    var state = State.WelcomeScreen

    lateinit var welcomeScreen: Screen
    lateinit var menuScreen: MenuScreen

    override fun create() {
        Gdx.input.inputProcessor = WrapCtx.mux

        welcomeScreen = MinimalScreen()
        menuScreen = MenuScreen()

        setScreen(welcomeScreen)
    }

    override fun render() {
        super.render()
        update()
    }

    private fun update() {
            when(state) {
                State.WelcomeScreen -> {
                    if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER)/* || timeout*/) {
                        setScreen(menuScreen)
                        state = State.Menu
                    }
                }
                State.Menu -> {
                    if (menuScreen.isPlay) {
                        val ctx = ContextImpl()
                        setScreen(GameScreen(ctx))
                        state = State.Game
                    }
                }
                State.Game -> {

                }
            }
    }

    override fun dispose() {
        super.dispose()

        welcomeScreen.dispose()
        menuScreen.dispose()

        Gdx.input.inputProcessor = null
        WrapCtx.dispose()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        WrapCtx.viewport.update(width, height, true)

        WrapCtx.camera.viewportWidth = width.toFloat()
        WrapCtx.camera.viewportHeight = height.toFloat()
        WrapCtx.camera.position.set(width / 2f, height / 2f, 0f)
        WrapCtx.camera.update()
    }
}




