package com.minimal.planet

/*class TestContext : Ctx {
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
                LifetimeSystem(engine),
                ActionsSystem(this),
                BodyDisposeSystem(engine))
    }
}

class SystemsTest : StringSpec() {
    init {
        "LifeTimeSystem should work" {
            val engine = MyEngine()
            engine.add(LifetimeSystem(engine))
            engine.entity{
                lifetime(1f)
            }
            engine.ents.size shouldBe 1
            engine.update(1f)
            engine.ents.size shouldBe 0
        }

        "Full engine should work" {
            val ctx = TestContext()
            ctx.engine.entity{
                lifetime(1f)
                body(
                        ctx.world.body {
                            circle(radius = 1f) {
                                density = 1f
                            }
                        }
                )
            }

            ctx.engine.ents.size shouldBe 1
            ctx.engine.update(1f)
            ctx.engine.ents.size shouldBe 0
        }
    }
}*/

