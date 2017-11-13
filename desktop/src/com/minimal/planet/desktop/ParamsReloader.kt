package com.minimal.planet.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
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

        props.forEach { key, value ->
            val valStr = value.toString()
            try {
                val setterName = "set" + key.toString()[0].toUpperCase() + key.toString().substring(1)
                val setter = Params::class.java.methods.firstOrNull { method ->
                    method.name == setterName
                }
                if (setter != null) {
                    val field = Params::class.java.getDeclaredField(key.toString())
                    when(field.type) {
                        Color::class.java -> {
                            val color = parseColorHex(valStr)
                            if (color != null)
                                setter.invoke(Params, color)
                        }
                        String::class.java -> setter.invoke(Params, valStr)
                        Int::class.java -> {
                            val int = parseInt(valStr)
                            if (int != null)
                                field.set(Params, int)
                        }
                        else -> throw IllegalArgumentException("Unsupported type: " + field.type)
                    }
                }
                else {
                    println("Params - setter not found: " + key)
                }
            }
            catch(e: NoSuchFieldException) {
                println("Params - unknown field: " + key)
            }
        }
    }

    fun parseInt(str: String?): Int? {
        if (str != null)
            try {
                return Integer.parseInt(str)
            }
            catch (e: NumberFormatException) {}
        return null
    }

    fun parseColorHex(str: String?): Color? {
        if (str != null)
            try {
                return Color.valueOf(str)
            }
            catch (e: NumberFormatException) {}
        return null
    }

    override fun run() {
        println("File reload thread started")
        while (!Thread.interrupted()) {
            Thread.sleep(500)
            if (lastModified < stdFile.lastModified()) {
                reload()
                println("Props updated")
            }
            lastModified = stdFile.lastModified()
        }
        println("File reload thread finished")
    }

}