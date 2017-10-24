package com.minimal.gdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.onClick

fun alphaButton(skin: Skin, region: String) =
        Button(
                skin.newDrawable(region, Color(1f, 1f, 1f, .3f)),
                skin.newDrawable(region, Color(1f, 1f, 1f, .6f)))

fun alphaButton(skin: Skin, region: String, onClick: () -> Unit): Button {
    val button = alphaButton(skin, region)
    button.onClick { onClick() }
    return button
}