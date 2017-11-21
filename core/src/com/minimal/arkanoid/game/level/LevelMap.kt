package com.minimal.arkanoid.game.level

class LevelMap(val w: Int, val h: Int) {
    val map = Array<Char>(w * h){i -> ' '}

    operator fun get(x: Int, y: Int): Char {
        assert(x < w) {"x out of bounds, x = $x, width = $w"}
        assert(y < h) {"y out of bounds, y = $y, height = $h"}
        return map[x + y * w]
    }
    operator fun set(x: Int, y: Int, field: Char) {
        assert(x < w) {"x out of bounds, x = $x, width = $w"}
        assert(y < h) {"y out of bounds, y = $y, height = $h"}
        map[x + y * w] = field
    }
}