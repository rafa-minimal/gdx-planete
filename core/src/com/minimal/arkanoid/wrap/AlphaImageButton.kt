package com.minimal.arkanoid.wrap

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class AlphaImageButton(imageUp: Drawable) : ImageButton(imageUp) {

    override fun draw(batch: Batch?, parentAlpha: Float) {
        var myAlpha = 0.5f
        if (isPressed && !isDisabled) {
            myAlpha = 1f
        }
        super.draw(batch, parentAlpha * myAlpha)
    }
}