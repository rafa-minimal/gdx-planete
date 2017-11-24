package com.minimal.arkanoid.game.level

import com.minimal.arkanoid.Params
import com.minimal.arkanoid.game.*
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.entity.entity
import com.minimal.arkanoid.game.level.LevelResult.None
import com.minimal.arkanoid.game.level.Tutorial.State.*
import com.minimal.arkanoid.game.script.Script
import java.util.*

class Tutorial(map: LevelMap, props: Properties) : Level(map, props) {
    var complete = false
    private var state = INIT
    //val stage = Wrap
    lateinit private var playe: MyEntity

    private enum class State {
        INIT,
        GO_LEFT,
        GO_RIGHT,
        HIT_BRICK,
        FINISH_IT,

        WAIT
    }

    fun emphasize() {

    }

    private var initialBricksCount = 0

    fun tutorial_update() {
        when (state) {
            INIT -> {
                emphasize() // left button
                state = GO_LEFT
            }
            GO_LEFT -> {
                if (playe[body].position.x <= 4f) {
                    emphasize() // right button
                    state = GO_RIGHT
                }
            }
            GO_RIGHT -> {
                if (playe[body].position.x >= width / 2f) {
                    buildBoxes()
                    initialBricksCount = ctx.engine.family(box).count()
                    state = WAIT
                    Actions.schedule(0.5f) {
                        createBallHooked(ctx)
                        Actions.schedule(0.5f) {
                            state = HIT_BRICK
                        }
                    }
                }
            }
            HIT_BRICK -> {
                if (playe[player].entsInRange.size > 0) {
                    emphasize() // fire button
                    // slow down, setTimeScale(0.5)
                } else {
                    // don't emphasize
                    // speed up: setTimeScale(1)
                }
                if (ctx.engine.family(box).count() != initialBricksCount) {
                    // print "great!"
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
        ctx.balls = -1
        ctx.levelTimeMs = -1

        ctx.engine.entity {
            script(TutorialScript())
        }

        Params.override(props)

        buildEdges()

        playe = createPlayer(ctx, width, Params.player_y)
    }

    override fun result(): LevelResult =
            if (complete) {
                super.result()
            } else {
                None
            }
}