package com.minimal.arkanoid.game.level

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.script.Script
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.level.LevelResult.None
import com.minimal.arkanoid.game.script.JointBreakScript
import com.minimal.utils.rnd
import ktx.box2d.*
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
                /*1 -> map[x, y] = '#'
                2 -> map[x, y] = '='
                3 -> map[x, y] = 'V'*/
            }
            map[x, y] = 'V'

        }

        // create boxes
        for (x in 0 until map.w) {
            print("|")
            for (y in 0 until map.h) {
                print(map[x,y])
                when(map[x,y]) {
                    '#' -> boxOneShot(x*2 + 1f, height / 3 + y + 0.5f)
                    '=' -> boxNaZawiasach(x*2 + 1f, height / 3 + y + 0.5f)
                    'V' -> boxDiament(x*2 + 1f, height / 3 + y + 0.5f)
                }
            }
            println("|")
        }

        // create player
        createPlayer(ctx, width, height, baseBody)

        // ball
        createBall(ctx)
    }

    fun boxOneShot(x: Float, y: Float): MyEntity {
        return ctx.engine.entity {
            body(ctx.world.body(StaticBody) {
                position.set(x, y)
                box(2f, 1f) {
                    density = 1f
                    restitution = 0.4f
                    filter {
                        categoryBits = default
                    }
                }
            })
            energy(10f)
        }
    }

    fun boxDiament(x: Float, y: Float) {
        boxOneShot(x, y).add(BoxDiamondScript(ctx))
    }

    fun boxNaZawiasach(x: Float, y: Float) {
        val body = ctx.world.body(DynamicBody) {
            position.set(x, y)
            linearDamping = 0.5f
            angularDamping = 0.5f
            box(2f, 1f) {
                density = 0.2f
                restitution = 1f
                filter {
                    categoryBits = default
                }
            }
        }
        val jl = baseBody.distanceJointWith(body) {
            length = 0f
            localAnchorA.set(x - 0.5f, y)
            localAnchorB.set(-0.5f, 0f)
            frequencyHz = 4f
            dampingRatio = 0.8f
        }
        val jr = baseBody.distanceJointWith(body) {
            length = 0f
            localAnchorA.set(x + 0.5f, y)
            localAnchorB.set(0.5f, 0f)
            frequencyHz = 4f
            dampingRatio = 0.8f
        }
        ctx.engine.entity {
            body(body)
            script(JointBreakScript(ctx, jl))
            script(JointBreakScript(ctx, jr))
        }
    }



    class BoxDiamondScript(val ctx: Context) : Script {
        override fun beforeDestroy(me: MyEntity) {
            createDiamond(ctx, me.get(body).position)
        }
    }

    fun result(): LevelResult {
        return None
    }
}

enum class LevelResult {
    Failed,
    TimesUp,
    Complete,
    None
}

