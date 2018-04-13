package com.minimal.arkanoid.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.minimal.arkanoid.Params
import com.minimal.arkanoid.ParamsDefaults
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.hud.BuildHud
import com.minimal.arkanoid.game.level.Level
import com.minimal.arkanoid.game.system.*
import com.minimal.arkanoid.wrap.WrapCtx
import com.minimal.ecs.Engine
import com.minimal.gdx.project
import com.minimal.gdx.unproject
import ktx.box2d.body
import ktx.math.vec2

typealias MyEngine = Engine<MyEntity>

class Context(val level: Level) {
    val engine = MyEngine()
    val world = World(vec2(0f, -10f), true)
    val baseBody = world.body(StaticBody) {}
    /*val baseBodyEnt = engine.entity {
        body(baseBody)
    }*/
    var timeMs = 0
    var levelTimeMs = 0

    val batch = SpriteBatch()
    val debugRenderer = Box2DDebugRenderer()
    val renderer = ShapeRenderer()

    val worldCamera = OrthographicCamera()
    val displayHeight = displayWorldHeight(level.width, level.height)
    val cameraSystem = CameraSystem(worldCamera, level.width, displayHeight)

    val atlas = TextureAtlas(Gdx.files.internal("atlas.atlas"))
    val tailTex = Texture("tail.png")

    val heroControl = SinglePlayerHeroControl()
    var lives: Int = 0
    var timeScale: Float = 1f

    val buildHud = BuildHud(this)

    fun displayWorldHeight(levelWidth: Float, levelHeight: Float): Float {
        // Preferowana wysokość, która pasuje do aspect ratio urządzenia
        val prefHeight = levelWidth * Gdx.graphics.height.toFloat() / Gdx.graphics.width.toFloat()
        val height = Math.max(prefHeight, levelHeight)
        return height
    }

    fun start() {
        Actions.reset()
        // level ustawia parametry (Params), więc musi być na początku
        Params = ParamsDefaults()
        level.start(this)

        cameraSystem.worldPosition.set(level.width/2, level.height-displayHeight/2)

        engine.add(
                WorldSystem(this),
                EnergySystem(engine),
                TailSystem(this),
                //GravitySystem(this),
                LifetimeSystem(engine),
                InputUpdateSystem(this),
                ScriptUpdateSystem(this),
                //AsteroidSpawnSystem(this),
                ActionsSystem(this),
                cameraSystem,
                TailRenderSystem(this),
                SpriteRenderSystem(this),
                SpriteSystem(this),
                WorldRenderSystem(this),
                //RangeDrawSystem(this),
                DebugRenderSystem(this),
                CleanUpSystem(this),
                ScriptBeforeDestroySystem(this),
                ParentChildSystem(this),
                BodyDisposeSystem(engine))
    }

    fun dispose() {

    }

    fun takeLive(): Boolean {
        if (lives == -1) {
            return true
        }
        if (lives > 0) {
            lives--
            return true
        }
        return false
    }

    val tmp = Vector2()

    fun worldToStage(worldLen: Float): Float {
        tmp.set(worldLen, 0f)
        return worldToStage(tmp).x
    }

    fun stageToWorld(stagePos: Vector2): Vector2 {
        val screenPos = WrapCtx.stage.stageToScreenCoordinates(stagePos)
        //val screenPos = WrapCtx.stage.viewport.project(stagePos)
        val worldPos = worldCamera.unproject(screenPos)
        return worldPos
    }

    fun worldToStage(worldPos: Vector2): Vector2 {
        val screenPos = worldCamera.project(worldPos)

        val stagePos = WrapCtx.stage.screenToStageCoordinates(screenPos)
        stagePos.y = WrapCtx.stage.viewport.getScreenHeight() - stagePos.y
        //val stagePos = WrapCtx.stage.viewport.unproject(screenPos)
        return stagePos
    }
}

class PlayerControl {
    var left: Boolean = false
    var right: Boolean = false
    var fire: Boolean = false
    var fireJustPressed: Boolean = false
}