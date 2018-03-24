package com.minimal.planet

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.ecs.Engine
import com.minimal.planet.game.InputUpdateSystem
import com.minimal.planet.game.SinglePlayerHeroControl
import com.minimal.planet.game.ents.HeroSystem
import com.minimal.planet.game.systems.SpriteSystem
import com.minimal.planet.level.Level
import ktx.math.vec2

typealias MyEngine = Engine<MyEntity>

private var ctx: Ctx? = null

fun ctx(): Ctx {
    return ctx!!
}

fun ctx(newCtx: Ctx) {
    ctx = newCtx
}

open class Ctx {
    var timeMs = 0

    val world = World(vec2(0f, 0f), true)
    val engine = MyEngine()
    val level = Level()

    val worldCamera = OrthographicCamera();
    val hudCamera = OrthographicCamera();

    val batch = SpriteBatch()
    val debugRenderer = Box2DDebugRenderer()
    val renderer = ShapeRenderer()

    val heroControl = SinglePlayerHeroControl();

    init {
        engine.add(
                InputUpdateSystem(),
                GravitySystem(this),
                WorldSystem(this),
                EnergySystem(engine),
                HeroSystem(),
                //GravitySystem(this),
                LifetimeSystem(engine),
                //AsteroidSpawnSystem(this),
                ActionsSystem(this),
                CameraSystem(this),
                SpriteSystem(),
                WorldRenderSystem(this),
                DebugRenderSystem(this),
                ScriptSystem(this),
                BodyDisposeSystem(engine))
        //engine.add()

    }

    fun dispose() {

    }
}

fun Float.toRad(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Int.toRad(): Float {
    return this * 2f * MathUtils.PI / 360f
}

fun Float.toDeg(): Float {
    return this / (2f * MathUtils.PI / 360f)
}

fun Int.toDeg(): Float {
    return this / (2f * MathUtils.PI / 360f)
}