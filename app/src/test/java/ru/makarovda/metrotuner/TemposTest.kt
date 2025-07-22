package ru.makarovda.metrotuner

import org.junit.Test
import ru.makarovda.metrotuner.domain.metronome.Tempos
import org.junit.Assert.*
class TemposTest {
    @Test
    fun convertTest(){

        for (i in 1..300){
            assertNotEquals("No Tempo", Tempos.getTempoName(i))
        }
        //assertNotEquals("No Tempo", Tempos.getTempoName(180))
    }
}