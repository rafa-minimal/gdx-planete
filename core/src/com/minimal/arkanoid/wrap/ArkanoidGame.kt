package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.minimal.arkanoid.game.level.LevelResult.*
import com.minimal.gdx.pressed

open class ArkanoidGame : CompoundScreenGame() {

    enum class State {
        WelcomeScreen,
        Menu,
        LevelScreen,
        Game
    }

    var state = State.WelcomeScreen

    lateinit var welcomeScreen: Screen
    lateinit var menuScreen: MenuScreen
    lateinit var levelScreen: LevelScreen
    var gameScreen: GameScreen? = null

    override fun create() {
        Gdx.input.inputProcessor = WrapCtx.mux

        welcomeScreen = MinimalScreen()
        menuScreen = MenuScreen()
        levelScreen = LevelScreen()

        setScreen(welcomeScreen)
    }

    override fun render() {
        super.render()
        update()
        if(Keys.ESCAPE.pressed()) {
            Gdx.app.exit()
        }
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
                        setScreen(levelScreen)
                        state = State.LevelScreen
                    }
                }
                State.LevelScreen -> {
                    if (levelScreen.isPlay) {
                        //gameScreen = GameScreen(levelScreen.getLevel())
                        gameScreen = GameScreen(levelScreen.currentLevel.toString())
                        setScreen(gameScreen!!)
                        state = State.Game
                    }
                }
                State.Game -> {
                    when(gameScreen?.result()) {
                        None -> null
                        TimesUp -> {
                            state = State.Menu
                            setScreen(menuScreen)
                        }
                        Complete -> {
                            state = State.Menu
                            setScreen(menuScreen)
                        }
                        Failed -> {
                            state = State.Menu
                            setScreen(menuScreen)
                        }
                    }
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




