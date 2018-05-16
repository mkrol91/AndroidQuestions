package com.example.mirek.androidquestions.splash

import org.junit.Test

class SplashActivityTest {

    @Test
    fun abcd() {
        noMoreLoops(7, { i ->
            System.out.println("currentValue:" + i)
        })
    }

    fun noMoreLoops(count: Int, action: Any.(Int) -> Unit) {
        var i = count - 1
        fun myLoop(currValue: Int) {
            action(currValue)
            if (currValue != 0) {
                --i
                myLoop(i)
            }
        }
        myLoop(i)
    }

    @Test
    fun otherTest() {
        arrayListOf("abcd", "efgh", "ijkl", "mnop").forEach(::printThis)
    }

    fun printThis(text: String) {
        System.out.print(text)
    }
}