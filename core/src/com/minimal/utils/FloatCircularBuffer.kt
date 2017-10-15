package com.minimal.utils

/**
 * FIFO Queue of primitive float values, constant capacity
 * @author ragg
 */
class FloatCircularBuffer(capacity: Int) {
    private val array: FloatArray
    private var head: Int = 0
    private var tail: Int = 0
    private var size: Int = 0

    init {
        assert(capacity > 0)
        array = FloatArray(capacity)
        head = 0
        tail = 0
        size = 0
    }

    /**
     * Push given value to the end (tail) of the queue (if queue is full, the head will be overwritten)
     */
    fun push(value: Float) {
        array[tail] = value
        moveTail()
        if (size == capacity()) {
            moveHead()
        } else {
            size++
        }
    }

    private fun moveTail() {
        tail++
        if (tail >= array.size) {
            tail = 0
        }
    }

    private fun moveHead() {
        head++
        if (head >= array.size) {
            head = 0
        }
    }

    /**
     * Remove and return the value from the beginning (head) of the queue
     */
    fun poll(): Float {
        if (size > 0) {
            val `val` = array[head]
            moveHead()
            size--
            return `val`
        } else {
            throw IndexOutOfBoundsException("Array empty, size: " + size)
        }
    }

    /**
     * Return (but don't remove) the value from the beginning (head) of the queue
     */
    fun peek(): Float {
        if (size > 0) {
            return array[head]
        } else {
            throw IndexOutOfBoundsException("Array empty, size: " + size)
        }
    }

    /**
     * Get value at given position from head
     */
    operator fun get(index: Int): Float {
        var index = index
        index += head
        if (index >= array.size) {
            index -= array.size
        }
        return array[index]
    }

    operator fun set(index: Int, `val`: Float) {
        var index = index
        index += head
        if (index >= array.size) {
            index -= array.size
        }
        array[index] = `val`
    }

    /**
     * Get number of elements in the queue
     */
    fun size(): Int {
        return size
    }

    /**
     * Get total capacity of the queue
     */
    fun capacity(): Int {
        return array.size
    }

    /**
     * Remove all elements
     */
    fun clear() {
        head = 0
        tail = 0
        size = 0
    }
}
