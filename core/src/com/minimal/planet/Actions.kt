package com.minimal.planet

import com.minimal.ecs.System
import java.util.*

class Action(var time: Int, val action: () -> Unit, val repeat: (() -> Int)? = null)

object actionOrder : Comparator<Action> {
    override fun compare(o1: Action?, o2: Action?): Int {
        if (o1 == null || o2 == null) {
            throw IllegalStateException("Null action in the queue")
        }
        return o1.time.compareTo(o2.time)
    }
}

object Actions {
    val queue = PriorityQueue<Action>(actionOrder)
    var currentTime: Int = 0
    fun schedule(delay: Int, action: () -> Unit) {
        queue.add(Action(currentTime + delay, action))
    }

    fun schedule(delay: Float, action: () -> Unit) {
        schedule((delay * 1000).toInt(), action)
    }

    fun every(interval: Int, action: () -> Unit) {
        queue.add(Action(currentTime + interval, action) {interval})
    }

    fun every(interval: Float, action: () -> Unit) {
        every((interval * 1000).toInt(), action)
    }

    fun update(currentTime: Int) {
        this.currentTime = currentTime
        while (queue.peek() != null && queue.peek().time <= currentTime) {
            val action = queue.poll()
            action.action()
            val repeat = action.repeat
            if(repeat != null) {
                action.time = currentTime + repeat()
                queue.add(action)
            }
        }
    }
}

class ActionsSystem(val ctx: Ctx) : System {
    override fun update(timeStepSec: Float) {
        Actions.update(ctx.timeMs)
    }
}
