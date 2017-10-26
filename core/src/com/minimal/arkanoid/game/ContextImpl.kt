package com.minimal.arkanoid.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.level.Level
import com.minimal.arkanoid.game.system.*
import com.minimal.ecs.Engine
import ktx.math.vec2

typealias MyEngine = Engine<MyEntity>

class Context(val level: Level) {
    val engine = MyEngine()
    val world = World(vec2(0f, -10f), true)
    var timeMs = 0

    val batch = SpriteBatch()
    val debugRenderer = Box2DDebugRenderer()
    val renderer = ShapeRenderer()

    val worldCamera = OrthographicCamera()
    val cameraSystem = CameraSystem(worldCamera, level.width, level.height)

    val atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
    val tailTex = Texture("tail.png")

    val playerControl = PlayerControl()

    fun start() {
        cameraSystem.worldPosition.set(level.width/2, level.height/2)

        engine.add(
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

        level.start(this)
    }

    fun dispose() {

    }
}

class PlayerControl {
    var left: Boolean = false
    var right: Boolean = false
    var fire: Boolean = false
    var fireJustPressed: Boolean = false
}