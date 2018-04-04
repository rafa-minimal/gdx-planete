package com.minimal.level

import com.minimal.arkanoid.game.level.loadLevelInternal
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import java.io.StringReader

class LevelTest : StringSpec() {

    val map1 = """
            |4:8
            |
            |   -|
            |   #|
            |x   |
            """.trimMargin()

    init {
        "level map should have correct size" {
            val data = StringReader(map1)
            val level = loadLevelInternal(data.buffered(), "1")
            level.height.toInt() shouldEqual 8
            level.width.toInt() shouldEqual 4
        }

        "level map should be aligned to bottom (fill top)" {
            val data = StringReader(map1)
            val level = loadLevelInternal(data.buffered(), "1")
            level.height.toInt() shouldEqual 8
            level.width.toInt() shouldEqual 4
            level.map[0, 0] shouldEqual 'x'
            level.map[3, 1] shouldEqual '#'
            level.map[3, 2] shouldEqual '-'
            level.map[3, 7] shouldEqual ' '
            level.map[0, 3] shouldEqual ' '
        }
    }
}