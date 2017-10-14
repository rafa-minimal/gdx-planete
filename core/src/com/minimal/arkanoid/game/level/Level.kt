package com.minimal.arkanoid.game.level

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.level.LevelResult.Complete
import com.minimal.arkanoid.game.level.LevelResult.None
import com.minimal.utils.rnd
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.vec2

class Level {
    lateinit var ctx : Context
    lateinit var baseBody: Body

    // najbardziej popularne aspect ratio dla androida, see: https://hwstats.unity3d.com/mobile/display-android.html
    // - 16:9 74%
    // - 5:3 (15:9) 22,2%
    // zrobimy świat odpowiadający aspect ratio 16:9
    val width = 18f
    val height = 32f

    val map = LevelMap(width.toInt() / 2, (height.toInt() * 2 / 3))

    fun edge(from: Vector2, to: Vector2) {
        ctx.engine.entity {
            body(ctx.world.body {
                edge(from, to) {
                    restitution = 1f
                    filter {
                        categoryBits = static
                    }
                }
            })
            // shakeScript
        }
    }

    fun start(ctx: Context) {
        this.ctx = ctx

        val baseBodyEnt = ctx.engine.entity {
            body(ctx.world.body(StaticBody) {})
        }
        baseBody = baseBodyEnt[body]

        // edges (box)
        edge(vec2(0f, 0f), vec2(0f, height))
        edge(vec2(width, 0f), vec2(width, height))
        edge(vec2(0f, height), vec2(width, height))

        // generate level
        repeat(10) {
            val x = rnd(0, map.w - 1)
            val y = rnd(0, map.h - 1)

            when(rnd(1, 3)) {
                1 -> map[x, y] = '#'
                2 -> map[x, y] = '='
                3 -> map[x, y] = 'V'
            }

        }

        // create boxes
        for (x in 0 until map.w) {
            print("|")
            for (y in 0 until map.h) {
                print(map[x,y])
                when(map[x,y]) {
                    '#' -> boxOneShot(ctx, x*2 + 1f, height / 3 + y + 0.5f)
                    '=' -> boxNaZawiasach(ctx, x*2 + 1f, height / 3 + y + 0.5f, baseBody)
                    'V' -> boxDiament(ctx, x*2 + 1f, height / 3 + y + 0.5f)
                }
            }
            println("|")
        }

        // create player
        createPlayer(ctx, width, height, baseBody)

        // ball
        createBall(ctx)
    }



    fun result(): LevelResult {
        var count = 0
        ctx.engine.family(box).foreach { entity, box -> count++ }
        if (count == 0) {
            return Complete
        }
        return None
    }
}

enum class LevelResult {
    Failed,
    TimesUp,
    Complete,
    None
}

