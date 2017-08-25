package com.minimal.planet

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.ecs.Engine
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

    override val world = World(vec2(0f, -30f), true)
    override val engine = MyEngine()
    override val level = Level()

    override val worldCamera = OrthographicCamera();

    val batch = SpriteBatch()
    override val debugRenderer = Box2DDebugRenderer()
    override val renderer = ShapeRenderer()

    init {
        engine.add(WorldSystem(this),
                EnergySystem(engine),
                TankControlSystem(this),
                LemingoLeaderSystem(this),
                LemingoSystem(this),
                //GravitySystem(this),
                LifetimeSystem(engine),
                //AsteroidSpawnSystem(this),
                ActionsSystem(this),
                CameraSystem(this),
                WorldRenderSystem(this),
                DebugRenderSystem(this),
                BodyDisposeSystem(engine))
        //engine.add()

        level.start(this)
    }

    fun dispose() {

    }
}

class Level {
    val worldRadius = 10f
    var ctx : Context? = null


    /*val createLemingo: () -> Unit = {
        lemingo(ctx!!, vec2(MathUtils.random(-100f, 100f), 5f))
    }*/

    fun start(ctx: Context) {
        //tank(ctx, vec2(0f, 8f))
        lemingo(ctx, vec2(0f, 4f))
        lemingo(ctx, vec2(-5f, 4f))
        lemingo(ctx, vec2(5f, 4f))

        this.ctx = ctx

        Actions.every(5f) {
            lemingo(ctx, vec2(MathUtils.random(-100f, 100f), 5f))
        }

        val from = vec2(0f, 0f)
        val to = vec2()
        for (i in (1).rangeTo(10)) {
            to.set(i * 10f, from.y + MathUtils.random(-3f, 3f))
            edge(ctx, from, to)
            from.set(to)
        }
        from.setZero()
        for (i in (-1).downTo(-10)) {
            to.set(i * 10f, from.y + MathUtils.random(-3f, 3f))
            edge(ctx, from, to)
            from.set(to)
        }
    }
}

fun Float.deg(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Int.deg(): Float {
    return this * 2f * MathUtils.PI / 360f
}