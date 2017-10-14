package com.minimal.planet

import com.minimal.arkanoid.game.Actions
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ActionsTest : StringSpec() {
    init {
        "Actions should work" {
            var count = 0
            Actions.schedule(10) {
                count += 1
            }
            Actions.update(9)
            count shouldBe 0
            Actions.update(10)
            count shouldBe 1
            Actions.update(100)
            count shouldBe 1
        }
    }
}