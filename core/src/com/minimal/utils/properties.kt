package com.minimal.utils

import java.util.*

fun Properties.getInt(key: String, default: Int): Int {
    val value = this.getProperty(key)
    if (value != null) {
        try {
            return Integer.parseInt(value)
        } catch (e: NumberFormatException) {
        }
    }
    return default
}

fun Properties.getFloat(key: String, default: Float): Float {
    val value = this.getProperty(key)
    if (value != null) {
        try {
            return java.lang.Float.parseFloat(value)
        } catch (e: NumberFormatException) {
        }
    }
    return default
}