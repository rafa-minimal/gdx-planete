package com.minimal.planet

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.minus

class JointBreakScript(val context: Context, val joint: DistanceJoint, val lengthThreshold: Float = 0.1f) : Script {

    var broken = false

    override fun update(me: MyEntity, timeStepSec: Float) {
        if (broken)
            return
        val vec = joint.bodyA.getWorldPoint(joint.localAnchorA) - joint.bodyB.getWorldPoint(joint.localAnchorB)
        val length = vec.len2()
        if (length > lengthThreshold) {
            context.world.destroyJoint(joint)
            broken = true
            // Tak się nie da, bo jesteśmy w trakcie iteracji po me.scripts, concurrent modification error, czy jakoś tak
            //me.scripts.remove(this)
        }
    }
}

object ballScript : Script {
    val threshold = 1f
    val factor = 5f

    override fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {
        if(other.contains(energy)) {
            val force = impulse.normalImpulses.sum()
            if (force > threshold) {
                other[energy] -= factor * force
            }
        }
    }
}
