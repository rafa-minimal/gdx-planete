package com.minimal.arkanoid.game.level

import java.io.BufferedReader
import java.util.*

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

    companion object {

        fun readWidthHeight(line: String) : Pair<Int, Int> {
            try {
                val size = line.trim().split(":")
                return Pair(Integer.parseInt(size[0]), Integer.parseInt(size[1]))
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Failed to parse size, expected <width>:<height>, got: " + line)
            }
        }

        fun load(reader: BufferedReader): LevelMap {
            val (width, height) = readWidthHeight(reader.readLine())

            var lines: List<String> = ArrayList()
            var line = reader.readLine()
            while (line != null) {
                if (line.startsWith("--")) {
                    break
                }
                if (line.startsWith(";")) {
                    line = reader.readLine()
                    continue
                }
                lines += line
                line = reader.readLine()
            }

            if (lines.size < height) {
                lines = List(height - lines.size){""} + lines
            }
            lines = lines.subList(0, height)
            lines = lines.map { it.trimEnd() }.reversed()

            val map = LevelMap(width, height)

            for (y in 0 until Math.min(map.h, lines.size)) {
                for (x in 0 until Math.min(map.w, lines[y].length)) {
                    map[x, y] = lines[y][x]
                }
            }
            return map
        }
    }
}