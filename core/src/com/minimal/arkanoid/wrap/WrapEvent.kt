package com.minimal.arkanoid.wrap

object WrapEvent {
    var event = ""
    fun set(event: String) {
        this.event = event
    }

    fun get(): String {
        return event
    }

    fun clear() {
        event = ""
    }
}