package com.minimal.planet

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.ecs.Engine
import com.minimal.planet.level.Level
import ktx.math.vec2


typealias MyEngine = Engine<MyEntity>

interface Context {
    val engine: MyEngine
    val world: World
    val level: Level
    val worldCamera: Camera
    val renderer: ShapeRenderer
    val debugRenderer: Box2DDebugRenderer
    var timeMs: Int
}

class ContextImpl : Context {
    override var timeMs = 0

    override val world = World(vec2(0f, 0f), true)
    override val engine = MyEngine()
    override val level = Level()

    override val worldCamera = OrthographicCamera();

    val batch = SpriteBatch()
    override val debugRenderer = Box2DDebugRenderer()
    override val renderer = ShapeRenderer()

    init {
        engine.add(
                GravitySystem(this),
                WorldSystem(this),
                EnergySystem(engine),
                LemingoLeaderSystem(this),
                LemingoSystem(this),
                //GravitySystem(this),
                LifetimeSystem(engine),
                //AsteroidSpawnSystem(this),
                ActionsSystem(this),
                CameraSystem(this),
                WorldRenderSystem(this),
                DebugRenderSystem(this),
                ScriptSystem(this),
                BodyDisposeSystem(engine))
        //engine.add()

        level.start(this)
    }

    fun dispose() {

    }
}

fun Float.deg(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Int.deg(): Float {
    return this * 2f * MathUtils.PI / 360f
}