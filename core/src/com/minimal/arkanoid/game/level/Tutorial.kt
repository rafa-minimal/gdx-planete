package com.minimal.arkanoid.game.level

import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.hud.ControlsHud
import com.minimal.arkanoid.game.level.LevelResult.None
import com.minimal.arkanoid.game.level.Tutorial.State.*
import com.minimal.arkanoid.game.script.Script
import java.util.*

class Tutorial(map: LevelMap, props: Properties, levelNumber: Int) : Level(map, props, levelNumber) {
    var complete = false
    private var state = INIT
    lateinit private var playe: MyEntity

    private var controlsHud: ControlsHud? = null

    fun setControlsHud(controlsHud: ControlsHud) {
        this.controlsHud = controlsHud
    }

    private enum class State {
        INIT,
        GO_LEFT,
        GO_RIGHT,
        HIT_BRICK,
        FINISH_IT,

        WAIT
    }

    private var initialBricksCount = 7

    fun tutorial_update() {
        when (state) {
            INIT -> {
                state = WAIT
                Actions.schedule(0.5f) {
                    controlsHud?.emphasizeLeft()
                    state = GO_LEFT
                }
            }
            GO_LEFT -> {
                if (playe[body].position.x <= 4f) {
                    controlsHud?.emphasizeEnd()
                    state = WAIT
                    Actions.schedule(0.5f) {
                        controlsHud?.emphasizeRight()
                        state = GO_RIGHT
                    }
                }
            }
            GO_RIGHT -> {
                if (playe[body].position.x >= width / 2f) {
                    controlsHud?.emphasizeEnd()
                    buildBoxes()
                    state = WAIT
                    Actions.schedule(1f) {
                        createBallHooked(ctx)
                        Actions.schedule(1f) {
                            state = HIT_BRICK
                        }
                    }
                }
            }
            HIT_BRICK -> {
                /*if (playe[hero].entsInRange.size > 0) {
                    controlsHud?.emphasizeFire()
                    // slow down, setTimeScale(0.5)
                } else {
                    controlsHud?.emphasizeEnd()
                    // speed up: setTimeScale(1)
                }*/
                if (ctx.engine.family(house).count() != initialBricksCount) {
                    // print "great!"
                    controlsHud?.emphasizeEnd()
                    state = FINISH_IT
                }
            }
            FINISH_IT -> {
                complete = true
            }
            WAIT -> {

            }
        }
    }

    inner class TutorialScript : Script {
        override fun update(me: MyEntity, timeStepSec: Float) {
            tutorial_update()
        }
    }

    override fun start(ctx: Context) {
        this.ctx = ctx
        ctx.lives = -1
        ctx.levelTimeMs = -1

        ctx.engine.entity {
            script(TutorialScript())
        }

        Params.override(props)

        buildEdges()

        //playe = createHero(ctx, width, Params.player_y)
    }

    override fun getResult(): LevelResult =
            if (complete) {
                super.getResult()
            } else {
                None
            }
}