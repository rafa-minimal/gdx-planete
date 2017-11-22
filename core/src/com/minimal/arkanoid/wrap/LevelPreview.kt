package com.minimal.arkanoid.wrap

import com.badlogic.gdx.math.Interpolation.pow2Out
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.minimal.arkanoid.game.level.LevelMap
import com.minimal.arkanoid.game.level.loadLevelMap
import com.minimal.gdx.WorldCamera
import com.minimal.utils.rnd
import ktx.actors.alpha
import ktx.actors.plus
import ktx.actors.then

class LevelPreview(level: String) {
    val camera = WorldCamera(10f, 10f)
    val viewport = ScreenViewport(camera)
    val stage: Stage = Stage(viewport, WrapCtx.batch)
    val boxDrawable = WrapCtx.skin.getDrawable("box")
    val boxes = mutableListOf<Actor>()

    var levelMap = loadLevelMap(level)

    init {
        initView(levelMap, 0f)
    }

    fun initView(map: LevelMap, direction: Float) {
        // stage.clear()

        val width = map.w * 2f
        val height = map.h * 3f / 2f

        for (x in 0 until map.w) {
            for (y in 0 until map.h) {
                when (map[x, y]) {
                    '#', '=', 'V' -> box(x * 2 - width / 2f, height / 3 + y - height / 3f, direction)
                }
            }
        }

        camera.setSize(width * 2, height * 2)
    }

    fun nextLevel(level: String) {
        explode(-1f)
        levelMap = loadLevelMap(level)
        initView(levelMap, 1f)
    }

    fun prevLevel(level: String) {
        explode(1f)
        levelMap = loadLevelMap(level)
        initView(levelMap, -1f)
    }

    private fun explode(direction: Float) {
        val w = levelMap.w.toFloat()
        val h = levelMap.h.toFloat()
        for (box in boxes) {
            val t = 0.4f + rnd(0.2f)
            //val t = 10f
            /*box.addAction(
                    Actions.parallel(
                            Actions.moveBy(-w, rnd(-h, h), t, pow2Out),
                            Actions.rotateBy(rnd(180f, 520f), t, pow2Out))
            )*/
            val action = parallel(
                    moveBy(direction * w, rnd(-h, h), t, pow2Out),
                    rotateBy(rnd(180f, 520f), t, pow2Out),
                    fadeOut(t)) then removeActor()
            //box.addAction(Actions.removeActor())
            box + action
        }
        boxes.clear()
    }

    fun draw() {
        stage.draw()
    }

    fun box(x: Float, y: Float, direction: Float) {
        val w = levelMap.w.toFloat()
        val h = levelMap.h.toFloat()
        val t = 0.4f + rnd(0.2f)

        val box = Image(boxDrawable)
        box.alpha = 0f
        box.setSize(2f, 1f)
        box.setPosition(direction * w, y + rnd(-h, h))
        box.rotation = rnd(180f, 520f)
        //box.setBounds(x, y, 2f, 1f)
        stage.addActor(box)
        box + parallel(
                moveTo(x - 1f, y - 0.5f, t, pow2Out),
                rotateTo(0f, t, pow2Out),
                fadeIn(t))
        boxes.add(box)
    }

    fun act(delta: Float) {
        stage.act(delta)
    }

    fun resize(w: Int, h: Int) {
        camera.resize(w, h)
    }
}