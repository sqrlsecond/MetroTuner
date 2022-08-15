package ru.makarovda.metrotuner

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.makarovda.metrotuner.utility.Averager

class AveragerTest {
    @Test
    fun averageIsCorrect(){
        val averager = Averager(2)
        averager.push(1)
        averager.push(3)
        assertEquals(averager.average, 2.toDouble(), 0.0001)
        averager.push(2)
    }

    @Test
    fun averageCircularCorrect(){
        val averager = Averager(2)
        averager.push(1)
        averager.push(3)
        averager.push(2)
        assertEquals(averager.average, 2.5, 0.0001)
    }
}