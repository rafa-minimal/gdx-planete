package com.minimal.arkanoid.game.script

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.minimal.arkanoid.game.entity.MyEntity

interface Script {
    fun update(me: MyEntity, timeStepSec: Float) {}
    fun beginContact(me: MyEntity, other: MyEntity, contact: Contact) {}
    fun endContact(me: MyEntity, other: MyEntity, contact: Contact) {}
    fun postSolve(me: MyEntity, other: MyEntity, contact: Contact, impulse: ContactImpulse) {}
    fun debugDraw(me: MyEntity, renderer: ShapeRenderer) {}
    fun beforeDestroy(me: MyEntity) {}
}