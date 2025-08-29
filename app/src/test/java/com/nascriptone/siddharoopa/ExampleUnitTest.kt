package com.nascriptone.siddharoopa

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.absoluteValue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun subtraction_isCorrect() {
        assertEquals(9, 10 - 1)
    }

    @Test
    fun absoluteValueIsCorrect() {
        val n = -3
        assertEquals(3, n.absoluteValue)
    }

}