package com.minimal.arkanoid.game.system

import com.minimal.arkanoid.game.MyEngine
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.arkanoid.game.lifetime
import com.minimal.ecs.ComponentTag
import com.minimal.ecs.System

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (C1) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                c1 ->
                action(c1)
            }
        }
    }
}

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (MyEntity, C1) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                ent, c1 ->
                action(ent, c1)
            }
        }
    }
}

fun <C1> system(engine: MyEngine, ct1: ComponentTag<C1>, action: (MyEntity, C1, Float) -> Unit): System {
    return object : System {
        val family = engine.family(ct1)
        override fun update(timeStepSec: Float) {
            family.foreach {
                ent, c1 ->
                action(ent, c1, timeStepSec)
            }
        }
    }
}

fun LifetimeSystem(engine: MyEngine) = system(engine, lifetime) {
    ent, lifetime, timeStepSec ->
    lifetime.lifetime -= timeStepSec
    if (lifetime.lifetime <= 0) {
        ent.dead = true
    }
}

/*class LifetimeSystem(val engine: MyEngine) : System {
    val family = engine.family(lifetime)
    override fun update(timeStepSec: Float) {
        family.foreach {
            ent, lifetime ->
            lifetime.lifetime -= timeStepSec
            if (lifetime.lifetime <= 0) {
                ent.dead = true
            }
        }
    }
}*/


