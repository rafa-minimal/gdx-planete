package com.minimal.planet

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.World
import com.minimal.ecs.Engine
import ktx.box2d.body
import ktx.math.vec2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer


typealias MyEngine = Engine<MyEntity>

interface Context {
    val engine: MyEngine
    val world: World
    val level: Level
    val worldCamera: Camera
    val debugRenderer: Box2DDebugRenderer
    var timeMs: Int
}

class ContextImpl : Context {
    override var timeMs = 0

    override val world = World(vec2(), true)
    override val engine = MyEngine()
    override val level = Level()

    override val worldCamera = OrthographicCamera();

    val batch = SpriteBatch()
    override val debugRenderer = Box2DDebugRenderer()

    init {
        engine.add(WorldSystem(this),
                EnergySystem(engine),
                RocketControlSystem(this),
                GravitySystem(this),
                LifetimeSystem(engine),
                AsteroidSpawnSystem(this),
                ActionsSystem(this),
                CameraSystem(this),
                WorldRenderSystem(this),
                BodyDisposeSystem(engine))
        //engine.add()

        level.start(this)
    }

    fun dispose() {

    }
}

class Level {
    val worldRadius = 20f
    fun start(ctx: Context) {
        rocket(ctx, vec2(0f, 8f))
        planet(ctx, 7f, vec2(), 3f.deg())
    }
}

private fun Float.deg(): Float {
    return this * 2f * MathUtils.PI / 360f
}
