package com.minimal.arkanoid.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.level.Level
import com.minimal.arkanoid.game.system.*
import com.minimal.ecs.Engine
import ktx.math.vec2

typealias MyEngine = Engine<MyEntity>

interface Context {
    val engine: MyEngine
    val world: World
    val level: Level
    val worldCamera: Camera
    val batch: SpriteBatch
    val renderer: ShapeRenderer
    val debugRenderer: Box2DDebugRenderer
    var timeMs: Int
    val atlas: TextureAtlas
    val tailTex: Texture
    val cameraSystem: CameraSystem
}

class ContextImpl : Context {
    override var timeMs = 0

    override val world = World(vec2(0f, -10f), true)
    override val engine = MyEngine()
    override val level = Level()

    override val worldCamera = OrthographicCamera()

    override val batch = SpriteBatch()
    override val debugRenderer = Box2DDebugRenderer()
    override val renderer = ShapeRenderer()
    override val atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
    override val tailTex = Texture("tail.png")
    override val cameraSystem = CameraSystem(worldCamera, level.width, level.height)


    init {
        cameraSystem.worldPosition.set(level.width/2, level.height/2)

        engine.add(
                PlayerControlUpdateSystem(this),
                WorldSystem(this),
                EnergySystem(engine),
                TailSystem(this),
                PlayerSystem(this),
                //GravitySystem(this),
                LifetimeSystem(engine),
                //AsteroidSpawnSystem(this),
                ActionsSystem(this),
                cameraSystem,
                SpriteRenderSystem(this),
                WorldRenderSystem(this),
                TailRenderSystem(this),
                RangeDrawSystem(this),
                DebugRenderSystem(this),
                CleanUpSystem(this),
                ScriptSystem(this),
                ParentChildSystem(this),
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