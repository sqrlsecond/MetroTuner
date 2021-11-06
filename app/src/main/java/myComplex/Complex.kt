package myComplex
import kotlin.math.*

/**
 * Класс для работы с комплексными числами
 */
class Complex(val re: Double, val im: Double) {
    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun plus(x: Double) = Complex(re + x, im)
    operator infix fun minus(x: Double) = Complex(re - x, im)
    operator infix fun times(x: Double) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Double) = Complex(re / x, im / x)
    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }
    val module: Double by lazy { sqrt(re*re + im*im) }

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }

    private val a = "%1.7f".format(re)
    private val b = "%1.7f".format(abs(im))

    /**
     * Exponential function
     * @param z input
     * @return exp(z)
     */
    companion object {

        fun exp(z: Complex): Complex {
            val r: Double = kotlin.math.exp(z.re)
            return Complex(r * cos(z.im), r * sin(z.im))
        }
    }
}

operator fun Double.plus(z: Complex) = z + this

operator fun Double.minus(z: Complex) = z - this