package com.minimal.planet

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import ktx.box2d.body
import ktx.math.vec2

class TestContext : Context {
    override val world = World(vec2(), true)
    override val engine = MyEngine()
    override val level = Level()
    override val worldCamera = OrthographicCamera()
    override val debugRenderer = Box2DDebugRenderer()
    override val renderer = ShapeRenderer()

    override var timeMs = 0

    init {
        engine.add(WorldSystem(this),
                EnergySystem(engine),
                TankControlSystem(this),
                LifetimeSystem(engine),
                AsteroidSpawnSystem(this),
                ActionsSystem(this),
                BodyDisposeSystem(engine))
    }
}

class SystemsTest : StringSpec() {
    init {
        "LifeTimeSystem should work" {
            val engine = MyEngine()
            engine.add(LifetimeSystem(engine))
            engine.add(entity{
                lifetime(1f)
            })
            engine.ents.size shouldBe 1
            engine.update(1f)
            engine.ents.size shouldBe 0
        }

        "Full engine should work" {
            val ctx = TestContext()
            ctx.engine.add(entity{
                lifetime(1f)
                body(
                        ctx.world.body {
                            circle(radius = 1f) {
                                density = 1f
                            }
                        }
                )
            })

            ctx.engine.ents.size shouldBe 1
            ctx.engine.update(1f)
            ctx.engine.ents.size shouldBe 0
        }
    }
}

