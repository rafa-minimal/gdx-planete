package com.minimal.arkanoid.game.level

class LevelMap(val w: Int, val h: Int) {
    val map = Array<Char>(w * h){i -> ' '}

    operator fun get(x: Int, y: Int): Char = map[x + y * w]
    operator fun set(x: Int, y: Int, field: Char) {
        map[x + y * w] = field
    }
}