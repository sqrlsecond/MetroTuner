import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.round

object FrequencyNoteConverter{

    val noteLiterals = listOf("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G","G#")

    fun convert(frequency: Double): NoteMus{
        if ((frequency <= 54.0) || (frequency >= 3500)){
            return NoteMus(noteLiterals[0], 0)
        }
        val semitonesCount = 12 * log2(frequency / 440.0)

        val octaveNumber = 4 + (semitonesCount / 12).toInt()

        val sign = if (semitonesCount > 0) -1 else 1

        var index = round(semitonesCount % 12).toInt()
        if (index < 0) {
            index += noteLiterals.size
        }
        if (index >= 12) index = 11

        val cents = (sign * (abs(semitonesCount % 12) - abs(round(semitonesCount % 12))) * 100).toInt()

        val noteName: String = noteLiterals[index] + octaveNumber.toString()

        return NoteMus(noteName, cents, frequency)
    }
}

data class NoteMus(val nearestNote: String, val cents: Int, val frequency: Double = 0.0)

