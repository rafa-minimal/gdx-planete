package com.minimal.arkanoid.wrap

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.minimal.arkanoid.game.level.LevelMap
import com.minimal.arkanoid.game.level.loadLevelMap
import com.minimal.gdx.WorldCamera

class LevelPreview(level: String) {
    val camera = WorldCamera(10f, 10f)
    val viewport = ScreenViewport(camera)
    val stage: Stage = Stage(viewport, WrapCtx.batch)
    val boxDrawable = WrapCtx.skin.getDrawable("box")

    var levelMap = loadLevelMap(level)

    init {
        initView(levelMap)
    }

    fun initView(map: LevelMap) {
        stage.clear()

        val width = map.w * 2f
        val height = map.h * 3f/2f

        for (x in 0 until map.w) {
            for (y in 0 until map.h) {
                when(map[x,y]) {
                    '#', '=', 'V' -> box(x*2 - width / 2f, height / 3 + y - height / 3f)
                }
            }
        }

        camera.setSize(width*2, height*2)
    }

    fun setLevel(level: String) {
        levelMap = loadLevelMap(level)
        initView(levelMap)
    }

    fun draw() {
        stage.draw()
    }

    fun box(x: Float, y: Float) {
        val box = Image(boxDrawable)
        box.setBounds(x, y, 2f, 1f)
        stage.addActor(box)
    }

    fun act(delta: Float) {
        stage.act(delta)
    }

    fun resize(w: Int, h: Int) {
        camera.resize(w, h)
    }
}