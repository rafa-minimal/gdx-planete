package com.minimal.arkanoid

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import java.io.File
import java.util.*

class Tuning(path: String) : Runnable {
    val file = Gdx.files.internal(path)
    val stdFile = File(path)
    var lastModified: Long
    var props: Properties
    var ints = mutableMapOf<String, Int>()
    val thread: Thread

    init {
        lastModified = stdFile.lastModified()
        props = Properties()
        val input = file.read()
        props.load(input)
        input.close()


        thread = Thread(this, "file-reloader")
        thread.isDaemon = true
        thread.start()
    }


    fun getString(key: String, default: String) = props.getProperty(key, default)

    fun getInt(key: String, default: Int): Int {
        return ints.getOrElse(key) {
            val prop = props.getProperty(key)
            if (prop == null) {
                ints.put(key, default)
                return default
            } else {
                val res = prop.toIntOrNull()
                if (res == null) {
                    ints.put(key, default)
                    return default
                }
                ints.put(key, res)
                return res
            }
        }
    }

    fun getColorHex(key: String, default: Color): Color {
        val res = props.getProperty(key)
        if (res != null)
            try {
                return Color.valueOf(res)
            }
            catch (e: NumberFormatException) {
                return default
            }
        else
            return default
    }

    override fun run() {
        println("File reload thread started")
        while (!Thread.interrupted()) {
            Thread.sleep(500)
            if (lastModified < stdFile.lastModified()) {
                val newProps = Properties()
                val input = file.read()
                newProps.load(input)
                input.close()
                props = newProps
                ints.clear()
                println("Props updated")
            }
            lastModified = stdFile.lastModified()
        }
        println("File reload thread finished")
    }

}