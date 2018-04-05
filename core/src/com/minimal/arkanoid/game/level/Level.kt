package com.minimal.arkanoid.game.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.level.LevelResult.None
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.script.ShakeScript
import com.minimal.utils.getInt
import com.minimal.utils.hsv
import com.minimal.utils.parseInt
import com.minimal.utils.rnd
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.vec2
import java.io.BufferedReader
import java.lang.Math.min
import java.util.*

enum class LevelResult {
    Failed,
    TimesUp,
    Complete,
    None
}

fun levelFile(levelNumber: String) = "level/" + levelNumber + ".txt"

fun loadLevelMap(level: String): LevelMap {
    when (level) {
        "random" -> return randomMap()
        else -> {
            if (Gdx.files.internal(levelFile(level)).exists()) {
                return loadLevel(level).map
            } else {
                return randomMap()
            }
        }
    }
}

fun loadLevel(levelNumber: String): Level {
    try {
        return loadLevelInternal(levelNumber)
    } catch (e: Throwable) {
        throw RuntimeException("Error reading level " + levelNumber, e)
    }
}

fun loadLevelInternal(levelNumber: String): Level {
    val reader = Gdx.files.internal(levelFile(levelNumber)).reader(1024)
    return loadLevelInternal(reader, levelNumber)
}

fun loadLevelInternal(reader: BufferedReader, levelNumber: String): Level {
    var line: String? = reader.readLine()
    var width: Int
    var height: Int
    try {
        val size = line!!.trim().split(":")
        width = Integer.parseInt(size[0])
        height = Integer.parseInt(size[1])
    } catch (e: RuntimeException) {
        println("Failed to parse first line <width>:<height> expected, got: " + line)
        width = 36 / 2
        height = 56
        println("Falling back to default size: " + width + ":" + height)
    }

    var lines: List<String> = ArrayList()
    line = reader.readLine()
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

    val props = Properties()
    props.load(reader)

    reader.close()
    if (lines.size < height) {
        lines = List(height - lines.size){""} + lines
    }
    lines = lines.subList(0, height)
    lines = lines.map { it.trimEnd() }.reversed()

    val map = LevelMap(width, height)

    for (y in 0 until min(map.h, lines.size)) {
        for (x in 0 until min(map.w, lines[y].length)) {
            map[x, y] = lines[y][x]
            //print(map[x, y])
        }
        //println("|")
    }
    if (props.containsKey("class")) {
        val className = "com.minimal.arkanoid.game.level." + props.getProperty("class")
        val levelClass = Class.forName(className)
        return levelClass.getConstructor(LevelMap::class.java, Properties::class.java, Int::class.java)
                .newInstance(map, props, parseInt(levelNumber, 0)) as Level
    } else {
        return Level(map, props, parseInt(levelNumber, 0))
    }
}

fun randomMap(): LevelMap {
    // najbardziej popularne aspect ratio dla androida, see: https://hwstats.unity3d.com/mobile/display-android.html
    // - 16:9       74%
    // - 5:3 (15:9) 22,2%
    // świat odpowiadający aspect ratio 16:9 -> 32:18
    val width = 18f + rnd(0, 18)
    val height = 32f + rnd(0, 32)

    val map = LevelMap(width.toInt() / 2, height.toInt())

    // generate level
    repeat(10) {
        val x = rnd(0, map.w - 1)
        val y = map.h / 3 + rnd(0, (map.h * 2 / 3) - 1)

        when (rnd(1, 3)) {
            1 -> map[x, y] = '#'
            2 -> map[x, y] = '='
            3 -> map[x, y] = 'V'
        }
    }
    return map
}

open class Level(val map: LevelMap, val props: Properties = Properties(), val levelNumber: Int) {
    lateinit var ctx: Context

    val width = map.w.toFloat()
    val height = map.h.toFloat()

    fun edge(from: Vector2, to: Vector2): MyEntity {
        val center = Vector2(to).add(from).scl(0.5f)
        from.sub(center)
        to.sub(center)
        return ctx.engine.entity {
            body(ctx.world.body {
                position.set(center)
                edge(from, to) {
                    restitution = Params.edge_restitution
                    filter {
                        categoryBits = static
                    }
                }
            })
            script(ShakeScript(ctx))
        }
    }

    var lastBallLeftRangeTime = 0
    var ballsInRange = 0
    val entityCountScript = object : Script {

        override fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {
            if (other.contains(ball)) {
                ballsInRange++
            }
        }

        override fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {
            if (other.contains(ball)) {
                ballsInRange--
                if (ballsInRange == 0) {
                    lastBallLeftRangeTime = ctx.timeMs
                }
            }
        }
    }

    open fun start(ctx: Context) {
        this.ctx = ctx
        ctx.balls = props.getInt("balls", 2)
        ctx.levelTimeMs = props.getInt("time_sec", 60) * 1000
        val invaderRows = props.getInt("invader_rows", 4)
        val invadersPerRow = props.getInt("invaders_per_row", 5)

        Params.override(props)
        val hue = levelNumber * 10f / 360f
        val c1 = hsv(hue, 1f/3f, 1f)
        val c2 = hsv(hue, 0.5f, 0.83f)
        val c3 = hsv(hue, 2f/3f, 2f/3f)
        val c4 = hsv(hue, 0.83f, 0.5f)
        val c5 = hsv(hue, 1f, 1/3f)

        Params.color_bg.set(c5)
        Params.color_ball.set(c1)
        Params.color_tail.set(c2)
        Params.color_box.set(c3)
        Params.color_hud.set(c4)

        /*ctx.engine.entity {
            body(ctx.world.body(StaticBody) {
                position.set(width / 2f, Params.player_y)
                box(width, 2 * Params.player_range) {
                    isSensor = true
                }
            })
            script(entityCountScript)
        }*/

        // edges (box)
        buildEdges()

        // create boxes
        buildBoxes()

        createInvaders(invaderRows,  invadersPerRow)

        // create player
        createPlayer(ctx, width, Params.player_y, ctx.heroControl)

        // ball
        //createBallHooked(ctx)
    }

    private fun createInvaders(invaderRows: Int, invadersPerRow: Int) {
        // spacing between invaders
        val s = (width - 2f) / invadersPerRow

        for (xi in 1..invadersPerRow) {
            for (yi in 1..invaderRows) {
                invader(ctx, (xi -1) * s + s/2f, yi * s + height/2f, yi)
            }
        }
    }

    fun buildBoxes() {
        for (y in 0 until map.h) {
            //print("|")
            for (x in 0 until map.w) {
                //print(map[x, y])
                when (map[x, y]) {
                    '#' -> box(ctx, x + 0.5f, y + 0.5f)
                    '=' -> floor(ctx, x + 0.5f, y + 0.5f)
                    'A' -> house(ctx, x + 0.5f, y + 0.5f)
                }
            }
            //println("|")
        }
    }

    fun buildEdges() {
        val left = edge(vec2(0f, -height), vec2(0f, height))
        left.add(texture, Texture(ctx.atlas.findRegion("fill"), 2f, 2 * height, vec2(-1f, 0f)))
        val right = edge(vec2(width, -height), vec2(width, height))
        right.add(texture, Texture(ctx.atlas.findRegion("fill"), 2f, 2 * height, vec2(1f, 0f)))
        val top = edge(vec2(0f, height), vec2(width, height))
        top.add(texture, Texture(ctx.atlas.findRegion("fill"), width + 4f, 2f, vec2(0f, 1f)))
    }

    open fun result(): LevelResult {
        /*var count = 0
        ctx.engine.family(box).foreach { entity, box -> count++ }
        if (count == 0) {
            return Complete
        }

        if (ctx.levelTimeMs != -1 && ctx.timeMs >= ctx.levelTimeMs) {
            return TimesUp
        }

        if (ctx.balls == 0) {
            // jeśli liczba piłek w grze = 0
            var ballCount = 0
            ctx.engine.family(ball).foreach { entity, ball -> ballCount++ }
            if (ballCount == 0) {
                println("Nie ma piłek w grze - Level Failed")
                return Failed
            }

            // jeśli są piłki, ale od czasu Params.level_no_balls_in_range_timeout, żadna nie była w zasięgu
            if (ballsInRange == 0 && ctx.timeMs > lastBallLeftRangeTime + Params.level_no_balls_in_range_timeout) {
                println("Nie ma piłek w zasięgu gracza od $Params.level_no_balls_in_range_timeout ms - Level Failed")
                return Failed
            }
        }*/

        return None
    }
}



