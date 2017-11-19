package com.minimal.arkanoid

import com.badlogic.gdx.graphics.Color
import java.util.*

var Params = ParamsDefaults()

class ParamsDefaults {
    var color_bg = Color.valueOf("5f0f0f")
    var color_ball = Color.valueOf("ffffff")
    var color_box = Color.valueOf("f05545")
    var box_width = 1.8f
    var box_height = 0.9f
    var box_render_width = 1.8f
    var box_render_height = 0.9f
    var player_vmax = 20f
    var ball_joint_threshold = 0.1f

    fun override(props: Properties) {
        for (key in props.keys) {
            val value = props[key]
            val valStr = value.toString()
            try {
                val setterName = "set" + key.toString()[0].toUpperCase() + key.toString().substring(1)
                val setter = Params::class.java.methods.firstOrNull { method ->
                    method.name == setterName
                }
                if (setter != null) {
                    val field = Params::class.java.getDeclaredField(key.toString())
                    when(field.type) {
                        Color::class.java -> {
                            val color = parseColorHex(valStr)
                            if (color != null)
                                setter.invoke(Params, color)
                        }
                        String::class.java -> setter.invoke(Params, valStr)
                        Int::class.java -> {
                            val int = parseInt(valStr)
                            if (int != null)
                                setter.invoke(Params, int)
                        }
                        Float::class.java -> {
                            val float = parseFloat(valStr)
                            if (float != null)
                                setter.invoke(Params, float)
                            println("    " + key + ": " + float)
                            println("    Params.ball...: " + Params.ball_joint_threshold)
                        }
                        else -> throw IllegalArgumentException("Unsupported type: " + field.type)
                    }
                }
                else {
                    println("Params - setter not found: " + key)
                }
            }
            catch(e: NoSuchFieldException) {
                println("Params - unknown field: " + key)
            }
        }
    }

    fun parseInt(str: String?): Int? {
        if (str != null)
            try {
                return Integer.parseInt(str)
            }
            catch (e: Exception) {}
        return null
    }

    fun parseFloat(str: String?): Float? {
        if (str != null)
            try {
                return java.lang.Float.parseFloat(str)
            }
            catch (e: Exception) {}
        return null
    }

    fun parseColorHex(str: String?): Color? {
        if (str != null)
            try {
                return Color.valueOf(str)
            }
            catch (e: Exception) {}
        return null
    }

    var player_y = 5f
    var player_range = 3f
    var player_radius = 0.5f
    var box_joint_threshold = 0.1f
    var ball_respawn_mode = "after_death"
    var ball_respawn_after_taken_delay = 3f
    var ball_autodestruction_timeout = 5f
}
