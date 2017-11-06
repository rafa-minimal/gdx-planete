package com.minimal.utils

class Every(val count: Int) {
    var counter = count
    fun go(oper: () -> Unit) {
        counter--
        if (counter == 0) {
            counter = count
            oper()
        }
    }
}