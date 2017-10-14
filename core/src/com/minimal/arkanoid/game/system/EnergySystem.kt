package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.MyEngine
import com.minimal.arkanoid.game.energy
import com.minimal.ecs.System

class EnergySystem(val engine: MyEngine) : System {
    val family = engine.family(energy)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, energy ->
            if (energy.energy <= 0f) {
                ent.dead = true
            }
        }
    }
}