package com.minimal.arkanoid.game.script

import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.minimal.arkanoid.game.Context
import com.minimal.arkanoid.game.entity.MyEntity
import com.minimal.utils.minus

class JointBreakScript(val context: Context,
                       val joint: DistanceJoint,
                       val lengthThreshold: Float = 0.1f,
                       val onBreak: (MyEntity) -> Unit = {}) : Script {

    var broken = false

    override fun update(me: MyEntity, timeStepSec: Float) {
        if (broken)
            return
        val vec = joint.bodyA.getWorldPoint(joint.localAnchorA) - joint.bodyB.getWorldPoint(joint.localAnchorB)
        val length = vec.len2()
        if (length > lengthThreshold) {
            context.world.destroyJoint(joint)
            broken = true
            onBreak(me)
            // Tak się nie da, bo jesteśmy w trakcie iteracji po me.scripts, concurrent modification error, czy jakoś tak
            //me.scripts.remove(this)
        }
    }
}