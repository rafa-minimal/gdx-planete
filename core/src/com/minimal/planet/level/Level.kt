package com.minimal.planet.level

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.PI2
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.minimal.*
import com.minimal.gdx.polarToEuclid
import com.minimal.planet.*
import com.minimal.planet.game.ents.createHero
import com.minimal.planet.game.ents.createInvader
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.filter
import ktx.box2d.revoluteJointWith
import kotlin.experimental.xor

class Level {
    val worldRadius = 10f
    lateinit var ctx : Ctx

    fun start(ctx: Ctx) {
        this.ctx = ctx

        val planet = ctx.engine.entity {
            body(ctx.world.body {
                circle(radius = worldRadius) {
                    restitution = 0f
                    filter {
                        categoryBits = ziemia
                    }
                }
            })
            gravity(100f)
        }

        for (i in 0..11) {
            ctx.engine.entity {
                parent(planet)
                sprite("planet") {
                    angleRad = i * MathUtils.PI2 / 12f
                    rotOrigY = - worldRadius + height / 2f
                    offsetY = worldRadius - height / 2f
                }
            }
        }

        /*val cityWidth = MathUtils.random(5f, 10f)
        repeat(4) {
            val pos = randomPos(0f, cityWidth)
            randomBuilding(pos)
        }

        repeat(5) {
            val pos = randomPosOnSurface()
            repeat(MathUtils.random(3, 7)) {
                randomGruz(pos)
            }
        }

        repeat(20) {
            randomTree(randomPosOnSurface())
        }*/

        repeat(8) {
            createInvader(rndAngle(), 10f, 1)
        }
        Actions.every(4f) {
            createInvader(rndAngle(), 10f, 1)
        }

        createStars(worldRadius)

        createHero(vec(0f, worldRadius + 1f), ctx().heroControl)
    }

    private fun randomTree(root: Vector2) {
        val width = MathUtils.random(1.3f, 2f)
        val height = MathUtils.random(1f, 2f)
        val normal = root.norm()
        val treeAngle = root.boxang()

        var pos = root + normal * (height/2f)
        val pien = ctx.engine.entity {
            body(ctx.world.body(StaticBody) {
                position.set(pos)
                angle = pos.boxang()
                box(width*0.1f, height) {
                    density = 1f
                    filter {
                        categoryBits = 0 // nigdy, z nikim
                    }
                }
            })
        }

        val scale = arrayOf(1.1f, 0.7f, 0.4f)
        var baseBody = pien[body]
        pos = root + normal * height
        repeat(3) { i ->
            val h = height * scale[i]
            val tree = ctx.engine.entity {
                body(ctx.world.body(DynamicBody) {
                    position.set(pos)
                    linearDamping = 0.3f
                    angle = treeAngle
                    gravityScale = -1f
                    polygon(vec(-scale[i]*width/2, 0f), vec(scale[i]*width/2, 0f), vec(0f, h)) {
                        density = 0.2f
                        filter {
                            categoryBits = ziemia
                            groupIndex = -1
                        }
                    }
                })
            }
            baseBody.revoluteJointWith(tree[body]) {
                localAnchorA.set(baseBody.getLocalPoint(pos))
            }
            pos = pos + normal * h
            baseBody.distanceJointWith(tree[body]) {
                localAnchorA.set(baseBody.getLocalPoint(pos))
                localAnchorB.set(tree[body].getLocalPoint(pos))
                frequencyHz = 1f
                dampingRatio = 0.9f
                length = 0f
            }
            baseBody = tree[body]
        }
    }

    private fun  randomGruz(pos: Vector2) {
        ctx.engine.entity {
            body(ctx.world.body(DynamicBody) {
                position.set(pos)
                linearDamping = 0.7f
                box(MathUtils.random(0.2f, 0.5f), MathUtils.random(0.2f, 0.5f)) {
                    density = 1f
                    friction = 1f
                    filter {
                        categoryBits = ziemia
                    }
                }
            })
            energy(5f)
        }
    }

    private fun randomBuilding(pos: Vector2) {
        val w = MathUtils.random(1f, 2f)
        val h = MathUtils.random(1f, 4f)

        val pos = pos + pos.norm() * (h/2f)
        ctx.engine.entity {
            body(ctx.world.body(StaticBody) {
                position.set(pos)
                angle = pos.boxang()
                box(w, h) {
                    density = 1f
                    filter {
                        categoryBits = ziemia
                        maskBits = all xor heroCat
                    }
                }
            })
            energy(10f)
        }
        ctx.engine.entity {
            body(ctx.world.body(StaticBody) {
                position.set(pos + pos.norm() * (h/2))
                angle = pos.boxang()
                polygon(vec(-w/2 * 1.1f, 0f), vec(w/2 * 1.1f, 0f), vec(MathUtils.random(-w/2, w/2), MathUtils.random(1f, 2f))) {
                    density = 1f
                    filter {
                        categoryBits = ziemia
                    }
                }
            })
            energy(10f)
        }
    }

    private fun randomPosOnSurface() = vec(0f, worldRadius).rotate(MathUtils.random(360f))

    private fun randomPos(angleDeg: Float, widthMeters: Float): Vector2 {
        val widthDeg = widthMeters / (2 * MathUtils.PI * worldRadius) * 360f
        val da = MathUtils.random(-widthDeg/2f, widthDeg/2f)
        return vec(0f, worldRadius).rotate(angleDeg + da)
    }
}

fun createStars(worldRadius: Float) {
    repeat(60) {
        val pos = polarToEuclid(random(PI2), worldRadius + random(20f))
        ctx().engine.entity {
            sprite("star-1") {
                size(MathUtils.random(0.1f, 0.2f))
            }
            position(pos)
        }
    }
    repeat(20) {
        ctx().engine.entity {
            val pos = polarToEuclid(random(PI2), worldRadius + random(20f))
            sprite("star-2") {
                size(MathUtils.random(0.2f, 0.5f))
                //posOffset(pos)
            }
            position(pos)
        }
    }
}