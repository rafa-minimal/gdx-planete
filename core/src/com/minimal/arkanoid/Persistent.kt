package com.minimal.arkanoid

import com.badlogic.gdx.Gdx

object Persistent {
    private val prefs = Gdx.app.getPreferences("Arkanoid")

    fun getLastLevel(): Int {
        return prefs.getInteger("lastLevel", 1)
    }

    fun setLastLevel(lastLevel: Int) {
        prefs.putInteger("lastLevel", lastLevel)
        prefs.flush()
    }

    fun getRecord(): Int {
        return prefs.getInteger("record", 0)
    }

    fun setRecord(record: Int) {
        prefs.putInteger("record", record)
        prefs.flush()
    }

    fun getTotalScore(): Int {
        return prefs.getInteger("totalScore", 0)
    }

    fun setTotalScore(totalScore: Int) {
        prefs.putInteger("totalScore", totalScore)
        prefs.flush()
    }

    fun getThruster(): Int {
        return prefs.getInteger("thruster", 0)
    }

    fun setThruster(thruster: Int) {
        prefs.putInteger("thruster", thruster)
        prefs.flush()
    }

    fun getGunPower(): Int {
        return prefs.getInteger("gunPower", 0)
    }

    fun setGunPower(gunPower: Int) {
        prefs.putInteger("gunPower", gunPower)
        prefs.flush()
    }

    fun getFireRate(): Int {
        return prefs.getInteger("fireRate", 0)
    }

    fun setFireRate(fireRate: Int) {
        prefs.putInteger("fireRate", fireRate)
        prefs.flush()
    }

    fun clear() {
        prefs.clear()
        prefs.flush()
    }
}