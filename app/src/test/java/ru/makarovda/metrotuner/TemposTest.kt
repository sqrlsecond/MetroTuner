package ru.makarovda.metrotuner

import org.junit.Test
import ru.makarovda.metrotuner.tempos.Tempos

class TemposTest {
    @Test
    fun convertTest(){

        assert(Tempos.getTempoName(120) == "Allegro")
    }
}