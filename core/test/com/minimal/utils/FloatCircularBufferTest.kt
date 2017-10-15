package com.minimal.utils

import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec

class FloatCircularBufferTest : WordSpec() {
    init {
        "FloatCircularBuffer" should {
            "hold floats up to capacity" {
                val buf = FloatCircularBuffer(4)
                buf.push(1f)
                buf.push(2f)
                buf.push(3f)
                buf.push(4f)

                buf.peek() shouldBe 1f
                buf[0] shouldBe 1f
                buf[1] shouldBe 2f
                buf[2] shouldBe 3f
                buf[3] shouldBe 4f

                buf.push(5f)
                buf[0] shouldBe 2f
                buf[1] shouldBe 3f
                buf[2] shouldBe 4f
                buf[3] shouldBe 5f

                buf.push(6f)
                buf.push(7f)
                buf.push(8f)
                buf.push(9f)
                buf[0] shouldBe 6f
                buf[1] shouldBe 7f
                buf[2] shouldBe 8f
                buf[3] shouldBe 9f

                buf should contain(6f, 7f, 8f, 9f)
            }
            "peek() should return last element" {
                val buf = FloatCircularBuffer(3)
                buf.push(1f)
                buf.peek() shouldBe 1f
                buf should contain(1f)
                buf.push(2f)
                buf.peek() shouldBe 1f
                buf should contain(1f, 2f)
                buf.push(3f)
                buf.peek() shouldBe 1f
                buf should contain(1f, 2f, 3f)
                buf.push(4f)
                buf.peek() shouldBe 2f
                buf should contain(2f, 3f, 4f)
                buf.push(5f)
                buf.peek() shouldBe 3f
                buf should contain(3f, 4f, 5f)
            }
            "poll() should return and remove last element" {
                val buf = FloatCircularBuffer(3)
                buf.push(1f)
                buf.push(2f)
                buf.push(3f)
                buf.push(4f)
                buf should contain(2f, 3f, 4f)
                buf.poll() shouldBe 2f
                buf.poll() shouldBe 3f
                buf.poll() shouldBe 4f
                buf.size() shouldBe 0
            }
        }
    }

    private fun contain(vararg farr: Float): (FloatCircularBuffer) -> Unit = {
        buffer: FloatCircularBuffer ->
        buffer.size() shouldBe farr.size
        for (i in 0 until buffer.size()) {
            buffer[i] shouldBe farr[i]
        }
    }
}