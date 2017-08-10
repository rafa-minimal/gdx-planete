package com.minimal.ecs

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class EcsTest : StringSpec() {
    init {
        val energy = ComponentTag<Int>(0)
        val body = ComponentTag<String>(1)

        "Entity contains should work" {
            val entity = Entity()
            entity.add(energy, 10)
            entity.add(body, "ball")

            entity.contains(energy) shouldBe true
            entity[energy] shouldBe 10

            entity.contains(body) shouldBe true
            entity[body] shouldBe "ball"
        }

        "Engine should handle entities" {

        }

        "Family1 should work" {
            val engine = Engine<Entity>()
            val family = engine.family(energy)
            val bodies = mutableListOf<Int>()
            family.foreach {
                energy -> bodies.add(energy)
            }
            bodies.size shouldBe 0

            val entity = Entity()
            entity.add(energy, 10)
            entity.add(body, "ball")
            engine.add(entity)

            family.foreach {
                energy -> bodies.add(energy)
            }
            bodies.size shouldBe 1
            bodies[0] shouldBe 10
        }

        "Family2 should work" {
            val engine = Engine<Entity>()
            val family = engine.family(energy, body)
            val bodies = mutableListOf<String>()
            family.foreach {
                energy, body -> bodies.add(body)
            }
            bodies.size shouldBe 0

            val entity = Entity()
            entity.add(energy, 10)
            entity.add(body, "ball")
            engine.add(entity)

            family.foreach {
                energy, body -> bodies.add(body)
            }
            bodies.size shouldBe 1
            bodies[0] shouldBe "ball"
        }
    }
}