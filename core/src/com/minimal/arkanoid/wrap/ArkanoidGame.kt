package com.minimal.arkanoid.wrap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.minimal.arkanoid.Persistent
import com.minimal.arkanoid.game.level.LevelResult.*
import com.minimal.gdx.justPressed

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
        WrapCtx.stage.act(Gdx.graphics.deltaTime)
        WrapCtx.stage.draw()
        WrapEvent.clear()
    }

    private fun update() {
        when (state) {
            State.WelcomeScreen -> {
                if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER)/* || timeout*/) {
                    setScreen(menuScreen)
                    state = State.Menu
                }
                if (Keys.ESCAPE.justPressed()) {
                    Gdx.app.exit()
                }
            }
            State.Menu -> {
                if (menuScreen.isPlay) {
                    setScreen(levelScreen)
                    state = State.LevelScreen
                }
                if (Keys.ESCAPE.justPressed()) {
                    Gdx.app.exit()
                }
            }
            State.LevelScreen -> {
                if (levelScreen.isPlay) {
                    //gameScreen = GameScreen(levelScreen.getLevel())
                    gameScreen = GameScreen(levelScreen.currentLevel.toString())
                    setScreen(gameScreen!!)
                    state = State.Game
                }
                if (Keys.ESCAPE.justPressed()) {
                    state = State.Menu
                    setScreen(menuScreen)
                }
            }
            State.Game -> {
                when (gameScreen?.result()) {
                    None -> null
                    TimesUp -> {
                        state = State.Menu
                        setScreen(menuScreen)
                    }
                    Complete -> {
                        val completedLevel = levelScreen.currentLevel
                        val nextLevel = completedLevel + 1
                        if (nextLevel > Persistent.getLastLevel()) {
                            Persistent.setLastLevel(nextLevel)
                        }
                        levelScreen.currentLevel = nextLevel
                        state = State.LevelScreen
                        setScreen(levelScreen)
                    }
                }
                if (Keys.ESCAPE.justPressed()) {
                    state = State.LevelScreen
                    setScreen(levelScreen)
                }
                when (WrapEvent.get()) {
                    "quit" -> {
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




