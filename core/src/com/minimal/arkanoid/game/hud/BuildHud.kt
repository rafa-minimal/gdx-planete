package com.minimal.arkanoid.game.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable.disabled
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.createBox
import com.minimal.arkanoid.wrap.WrapCtx
import ktx.actors.then

class ImageActor(val region: TextureRegion) : Actor() {
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch!!.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }
}

class BuildHud(val ctx: Context) {
    val back = Actor()
    val box = ImageActor(WrapCtx.gameAtlas.findRegion("box-4"))
    val boxShadow = ImageActor(WrapCtx.gameAtlas.findRegion("box-4"))
    val square = ImageActor(WrapCtx.gameAtlas.findRegion("square-target"))
    var boxLeft: Int = 0
    var stage: Stage? = null
    var boxSize = 0f
    var squareSize = 0f

    init {
        box.touchable = disabled
        boxShadow.touchable = disabled
        square.touchable = disabled

        back.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                square.remove()
                square.clearActions()
                updatePosition(x, y)
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                updatePosition(x, y)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                tmp.set(boxShadow.getX(Align.center), boxShadow.getY(Align.center))
                ctx.stageToWorld(tmp)
                createBox(ctx, tmp.x, tmp.y)
                newBox()
            }
        })
    }

    val tmp = Vector2()

    private fun updatePosition(x: Float, y: Float) {
        tmp.set(x, y + boxSize)
        box.setPosition(tmp.x, tmp.y, Align.center)
        ctx.stageToWorld(tmp)
        tmp.set(MathUtils.floor(tmp.x).toFloat(), MathUtils.floor(tmp.y).toFloat())
        ctx.worldToStage(tmp)
        boxShadow.setPosition(tmp.x, tmp.y)
    }

    fun activate(stage: Stage, boxCount: Int) {
        this.stage = stage
        boxLeft = boxCount

        boxSize = ctx.worldToStage(1f)
        squareSize = boxSize * 1.3f
        back.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        box.setSize(boxSize, boxSize)
        box.setOrigin(Align.center)
        boxShadow.setSize(boxSize, boxSize)
        boxShadow.setOrigin(Align.center)
        //boxShadow.color.a = 0.5f
        square.setSize(squareSize, squareSize)
        square.setOrigin(Align.center)

        stage.addActor(back)
        newBox()
    }

    fun deactivate() {
        back.remove()
        box.remove()
        boxShadow.remove()
        square.remove()
    }

    private fun newBox() {
        if (boxLeft <= 0) {
            ctx.level.play()
            deactivate()
            return
        }
        boxLeft--

        box.setPosition(back.width / 2f, back.height / 2f, Align.center)
        square.setPosition(back.width / 2f, back.height / 2f, Align.center)
        square.clearActions()
        square.addAction(
                Actions.forever(Actions.scaleTo(1.2f, 1.2f, 0.3f, Interpolation.pow2Out) then
                Actions.scaleTo(1f, 1f, 0.3f, Interpolation.pow2InInverse)))
        stage?.addActor(box)
        stage?.addActor(square)
        stage?.addActor(boxShadow)
    }
}