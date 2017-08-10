package com.minimal.ecs

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.math.vec2

class ComponentTag<T>(val index: Int)
private typealias CT<T> = ComponentTag<T>
object NoComp

open class Entity {
    var dead: Boolean = false
    val comps = Array<Any>(32) { NoComp }

    fun <T: Any> add(tag: CT<T>, c: T) {
        comps[tag.index] = c
    }

    operator fun <T> get(tag: CT<T>): T {
        return comps[tag.index] as T
    }

    fun <T> contains(tag: CT<T>): Boolean {
        return comps[tag.index] !is NoComp
    }
}

interface System {
    fun update(timeStepSec: Float)
}

class Engine<E: Entity> {
    val ents = mutableListOf<E>()
    val entsToAdd = mutableListOf<E>()
    val systems = mutableListOf<System>()

    private var updating: Boolean = false

    fun <C1> family(c: CT<C1>) = Family1(this, c)
    fun <C1, C2> family(c1: CT<C1>, c2: CT<C2>) = Family2(this, c1, c2)
    fun add(e: E) {
        when(updating) {
            true -> entsToAdd.add(e)
            false -> ents.add(e)
        }
    }

    fun add(s: System) {
        systems.add(s)
    }

    fun add(vararg s: System) {
        systems.addAll(s)
    }

    fun update(timeStepSec: Float) {
        updating = true
        for (s in systems) {
            s.update(timeStepSec)
        }
        ents.removeIf{ it.dead }
        updating = false
        ents.addAll(entsToAdd)
        entsToAdd.clear()
    }
}

class Family1<E: Entity, C1>(val engine: Engine<E>, val c1: CT<C1>) {
    fun foreach(action: (C1) -> Unit) {
        engine.ents.forEach {
            if (it.contains(c1)) {
                action(it[c1])
            }
        }
    }
    fun foreach(action: (E, C1) -> Unit) {
        engine.ents.forEach {
            if (it.contains(c1)) {
                action(it, it[c1])
            }
        }
    }
}

class Family2<E: Entity, C1, C2>(val engine: Engine<E>, val c1: CT<C1>, val c2: CT<C2>) {
    fun foreach(action: (C1, C2) -> Unit) {
        engine.ents.forEach {
            if (it.contains(c1) && it.contains(c2)) {
                action(it[c1], it[c2])
            }
        }
    }
    fun foreach(action: (E, C1, C2) -> Unit) {
        engine.ents.forEach {
            if (it.contains(c1) && it.contains(c2)) {
                action(it, it[c1], it[c2])
            }
        }
    }
}





/*fun main(args: Array<String>) {

    val rocket = Entity()
    rocket.add(energy, Energy(10f))
    rocket.add(bullet, Bullet(10f))

    val world = World(vec2(), true)

    val rocket2 = entity {
        body(
                world.body(DynamicBody) {
                    fixture(Circle) {
                        radius = 1f
                    }
                }
        )
        energy(10f)
    }

    val bullet = Entity() +
            Energy(1f) +
            Bullet(1f)

    engine.add(bullet)
    engine.update(1/60f)
    rocket[energy] -= 10f
    bullet.dead = true

    *//*val entity = Entity()
    entity.get(energy).energy
    entity[energy].energy*//*
}*/


