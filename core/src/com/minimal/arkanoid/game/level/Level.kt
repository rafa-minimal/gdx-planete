package com.minimal.arkanoid.game.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.level.LevelResult.*
import com.minimal.arkanoid.game.script.ShakeScript
import com.minimal.utils.getInt
import com.minimal.utils.rnd
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.vec2
import java.lang.Math.abs
import java.lang.Math.max
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
    val reader = Gdx.files.internal(levelFile(levelNumber)).reader(1024)
    var lines: List<String> = ArrayList<String>()

    var line: String? = reader.readLine()
    while (line != null) {
        if (line.startsWith("--")) {
            break
        }
        lines += line
        line = reader.readLine()
    }

    val props = Properties()
    props.load(reader)

    reader.close()
    lines = lines.map { it.trimEnd() }.reversed()
    val width = lines.map { li -> li.length }.max()
    val height = lines.size

    val map = LevelMap(width!!, height)

    for (y in 0 until map.h) {
        print("|")
        for (x in 0 until map.w) {
            map[x, y] = lines[y].get(x)
            print(map[x, y])
        }
        println("|")
    }
    return Level(map, props)
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

open class Level(val map: LevelMap, val props: Properties = Properties()) {
    lateinit var ctx: Context
    lateinit var baseBody: Body

    val width = map.w * 2f
    val height = map.h * 3f / 2f

    fun edge(from: Vector2, to: Vector2): MyEntity {
        val center = Vector2(to).add(from).scl(0.5f)
        from.sub(center)
        to.sub(center)
        return ctx.engine.entity {
            body(ctx.world.body {
                position.set(center)
                edge(from, to) {
                    restitution = 1f
                    filter {
                        categoryBits = static
                    }
                }
            })
            val width = max(abs(from.x - to.x), 1f)
            val height = max(abs(from.y - to.y), 1f)
            texture(ctx.atlas.findRegion("fill"), width, height)
            script(ShakeScript(ctx))
        }
    }

    open fun start(ctx: Context) {
        this.ctx = ctx
        ctx.balls = props.getInt("balls", 2)
        ctx.levelTimeMs = props.getInt("time_sec", 60) * 1000

        Params.override(props)

        val baseBodyEnt = ctx.engine.entity {
            body(ctx.world.body(StaticBody) {})
        }
        baseBody = baseBodyEnt[body]

        // edges (box)
        edge(vec2(0f, 0f), vec2(0f, height))
        edge(vec2(width, 0f), vec2(width, height))
        edge(vec2(0f, height), vec2(width, height))

        // create boxes
        for (y in 0 until map.h) {
            print("|")
            for (x in 0 until map.w) {
                print(map[x, y])
                when (map[x, y]) {
                    '#' -> boxOneShot(ctx, x * 2 + 1f, y + 0.5f)
                    '=' -> boxNaZawiasach(ctx, x * 2 + 1f, y + 0.5f, baseBody)
                    'V' -> boxDiament(ctx, x * 2 + 1f, y + 0.5f)
                }
            }
            println("|")
        }

        // create player
        createPlayer(ctx, width, 5f, baseBody)

        // ball
        createBall(ctx)
    }

    fun result(): LevelResult {
        var count = 0
        ctx.engine.family(box).foreach { entity, box -> count++ }
        if (count == 0) {
            return Complete
        }

        if (ctx.timeMs >= ctx.levelTimeMs) {
            return TimesUp
        }

        var ballCount = 0
        ctx.engine.family(ball).foreach { entity, ball -> ballCount++ }
        if (ctx.balls == 0 && ballCount == 0) {
            return Failed
        }
        return None
    }
}



