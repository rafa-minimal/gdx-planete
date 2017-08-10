package com.minimal.planet

import java.util.PriorityQueue
import com.minimal.ecs.System

class Action(val time: Int, val action: () -> Unit)

object actionOrder : Comparator<Action> {
    override fun compare(o1: Action?, o2: Action?): Int {
        if (o1 == null || o2 == null) {
            throw IllegalStateException("Null action in the queue")
        }
        return o1.time.compareTo(o2.time)
    }
}

object Actions {
    val queue = PriorityQueue<Action>()
    var currentTime: Int = 0
    fun schedule(delay: Int, action: () -> Unit) {
        queue.add(Action(currentTime + delay, action))
    }

    fun schedule(delay: Float, action: () -> Unit) {
        schedule((delay * 1000).toInt(), action)
    }

    fun update(currentTime: Int) {
        this.currentTime = currentTime
        while (queue.peek() != null && queue.peek().time <= currentTime) {
            val ac = queue.poll().action()
        }
    }
}

class ActionsSystem(val ctx: Context) : System {
    override fun update(timeStepSec: Float) {
        Actions.update(ctx.timeMs)
    }
}
