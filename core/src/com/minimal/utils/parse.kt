package com.minimal.utils

fun parseInt(str: String?, default: Int): Int {
    if (str != null)
        try {
            return Integer.parseInt(str)
        }
        catch (e: Exception) {}
    return default
}

fun parseIntOrNull(str: String?): Int? {
    if (str != null)
        try {
            return Integer.parseInt(str)
        }
        catch (e: Exception) {}
    return null
}