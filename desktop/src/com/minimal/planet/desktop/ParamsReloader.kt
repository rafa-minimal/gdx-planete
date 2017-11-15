package com.minimal.planet.desktop

import com.badlogic.gdx.Gdx
import com.minimal.arkanoid.Params
import java.io.File
import java.util.*

class ParamsReloader(path: String) : Runnable {
    val file = Gdx.files.internal(path)
    val stdFile = File(path)
    var lastModified: Long = 0
    val thread: Thread

    init {
        reload()

        thread = Thread(this, "file-reloader")
        thread.isDaemon = true
        thread.start()
    }

    fun reload() {
        lastModified = stdFile.lastModified()
        val input = file.read()
        val props = Properties()
        props.load(input)
        input.close()

        Params.override(props)
    }

    override fun run() {
        println("File reload thread started")
        while (!Thread.interrupted()) {
            Thread.sleep(500)
            if (lastModified < stdFile.lastModified()) {
                try {
                    reload()
                } catch(e: Exception) {
                    println("Failed to reload properties: " + e.message)
                    continue
                }
                println("Props updated")
            }
            lastModified = stdFile.lastModified()
        }
        println("File reload thread finished")
    }

}