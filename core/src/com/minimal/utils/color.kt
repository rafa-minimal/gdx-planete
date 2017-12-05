package com.minimal.utils

import com.badlogic.gdx.graphics.Color
import java.awt.Color as JColor

// HSV = HSB (Hue Saturation Value, Hue Saturation Brightness)
fun hsv(h: Float, s: Float, b: Float): Color {
    val c = JColor.getHSBColor(h, s, b)
    return Color(c.red/255f, c.green/255f, c.blue/255f, 1f)
}