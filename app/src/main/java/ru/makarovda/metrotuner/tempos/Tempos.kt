package ru.makarovda.metrotuner.tempos

object Tempos {

    data class Tempo(val name: String, val min: Int, val max: Int)

    val tempos: List<Tempo> = ArrayList<Tempo>(19).apply{
        //add(Tempo("Larghissimo", 1, 25))
        add(Tempo("Grave", 1, 40))
        add(Tempo("Lento/Largo", 41, 60))
        //add(Tempo("Largo ", 51, 55))
        //add(Tempo("Larghetto", 56, 60))
        add(Tempo("Adagio", 61,72))
        add(Tempo("Adagietto", 73, 80))
        //add(Tempo("Andantino", 81, 84))
        add(Tempo("Andante", 86, 96))
        //add(Tempo("Andante moderato", 91, 96))
        add(Tempo("Moderato", 97, 108))
        //add(Tempo("Allegro moderato", 109, 112))
        add(Tempo("Allegretto", 109, 119))
        add(Tempo("Allegro", 120, 132))
        add(Tempo("Vivace", 133, 144))
        //add(Tempo("Vivacissimo (Allegrissimo)", 145, 168))
        add(Tempo("Presto", 145, 165))
        add(Tempo("Prestissimo", 166, 200))
    }

    val temposCount = tempos.size

    fun getTempoName(bpm: Int): String{
        if((bpm < tempos[0].min) || (bpm > tempos.last().max)) {
            return "No Tempo"
        }

        return tempos[binarySearch(bpm,0, tempos.lastIndex)].name

    }

    private fun binarySearch(bpm: Int, low: Int, high: Int): Int{
        val mid = (low + high) / 2

        if(bpm < tempos[mid].min) {
            return binarySearch(bpm, low, mid)
        }
        if (bpm > tempos[mid].max) {
            return binarySearch(bpm, mid, high)
        }

        return mid
    }
}