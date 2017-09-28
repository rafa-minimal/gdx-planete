package com.minimal.planet.level

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.*
import com.minimal.planet.*
import ktx.box2d.*
import ktx.math.vec2
import kotlin.experimental.xor

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
            val x = rnd(0, map.w-1)
            val y = rnd(0, map.h-1)
            map[x, y] = '#'
        }

        // create boxes
        for (x in 0 until map.w) {
            print("|")
            for (y in 0 until map.h) {
                print(map[x,y])
                if (map[x, y] == '#') {
                    createBox(x*2 + 1f, height / 3 + y + 0.5f)
                }
            }
            println("|")
        }

        // create player
        createPlayer()

        // ball
        createBall(ctx)
    }

    private fun createBox(x: Float, y: Float) {
        when(rnd(1,2)) {
            1 -> {
                ctx.engine.entity {
                    body(ctx.world.body(StaticBody) {
                        position.set(x, y)
                        box(2f, 1f) {
                            density = 1f
                            restitution = 1f
                            filter {
                                categoryBits = default
                            }
                        }
                    })
                    energy(10f)
                }
            }
            2 -> {
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
        }

    }

    private fun createPlayer() {
        val pos = vec2(width / 2, height / 6)
        val playerRadius = 0.5f
        val playerRange = 3f

        val body = ctx.world.body(DynamicBody) {
            position.set(pos)
            fixedRotation = true
        }

        val mainFixture = body.circle(playerRadius) {
            density = 3f
            filter {
                categoryBits = default
                maskBits = 0
            }
        }
        val rangeFixture = body.circle(playerRange) {
            isSensor = true
            filter {
                categoryBits = default
                maskBits = all xor static
            }
        }

        val player = ctx.engine.entity {
            body(body)
            player(rangeFixture)
            script(PlayerRangeScript)
        }

        val limit = width/2 - playerRadius

        baseBody.prismaticJointWith(body) {
            localAnchorA.set(pos)
            localAxisA.set(1f, 0f)
            enableLimit = true
            lowerTranslation = -limit
            upperTranslation = limit
        }
    }
}

