package ru.makarovda.metrotuner.ui.metronome

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.makarovda.metrotuner.R


class LimitChecker(
    var lowLimit: Float,
    var highLimit: Float)
{
    fun checkValue(checkedValue: Float): Float {
        if (checkedValue < lowLimit) return lowLimit
        if (checkedValue > highLimit) return highLimit
        return checkedValue
    }
}

class LineConverter(
    x1: Float,
    x2: Float,
    y1: Float,
    y2: Float
)
{
    var k = 0.0f
        private set

    var b = 0.0f
        private set

    var k_rev = 0.0f
        private set

    var b_rev = 0.0f
        private set

    init {
        initialize(x1, x2, y1, y2)
    }

    fun convert(x: Float): Float {
        return x * k + b
    }

    fun initialize(x1: Float, x2:Float, y1: Float, y2: Float){

        if ((x2 - x1 != 0.0f) && (y2 - y1 != 0.0f)) {
            k = (y2-y1) / (x2-x1)
            b = y1 - k * x1

            k_rev = (x2-x1)/(y2-y1)
            b_rev = x1 - k_rev * y1
        }


    }

    fun revConvert(y: Float): Float {
        return  y * k_rev + b_rev
    }
}

class VerticalSlider @JvmOverloads constructor(context: Context,
                     attrs: AttributeSet? = null): View(context, attrs) {

    //Нижняя граница диапазона
    private var minValue: Float = 0.0f

    //Верхняя граница диапазона
    private var maxValue: Float = 100.0f

    //Перевод из значения слайдера в пиксели
    private val converter: LineConverter = LineConverter(0f, 100f, 1f, 1000f)

    //Проверка на соответсвие границам диапазона
    private val limiter: LimitChecker = LimitChecker(1f, 1000f)

    //Дополнительный отступ по вертикали
    private var additionalPadding: Int = 0

    //Смещение по оси X для линий
    private var lineOffsetX: Float = 0.0f

    //Смещение по оси X для меток
    private var textOffset: Float = 0.0f

    //Обработка изменения позиции слайдера
    var valueChangeHandler: (Float) -> Unit = {}

    private var lineStart = 0.0f

    private var sliderPos = 0.0f
    //Метки для слайдера
    var labels: Map<String, Float>? = null
        set(value) {
            field = value
            invalidate()
        }


    private val painter = Paint().apply{
        setColor(Color.BLUE)
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 80.0f
        //textSize = 20.0f
    }

    private val textPainter = Paint().apply {
        setColor(Color.BLACK)
        textSize = 20.0f
    }

    private var lineStrokeWidth: Float = 40.0f

    private var lineEnd = 0.0f

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VerticalSlider,
            0, 0).apply {
                lineStrokeWidth = getDimension(R.styleable.VerticalSlider_strokeWidth, 40.0f)
                textPainter.textSize = getDimension(R.styleable.VerticalSlider_textSize, 100.0f)
            }
        additionalPadding = (lineStrokeWidth / 2).toInt()
        lineEnd = additionalPadding.toFloat()
    }



    fun initializeConverter(minValue: Float, maxValue: Float){
        this.minValue = minValue
        this.maxValue = maxValue
        converter.initialize(additionalPadding.toFloat(), (height - additionalPadding).toFloat(), maxValue, minValue)
    }

    fun setSliderPosition(pos: Float){
        if((pos >= minValue) && (pos <= maxValue)) {
            sliderPos = pos
        }
    }

    fun getSliderPos(): Float{
        return converter.convert(lineEnd)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)


        converter.initialize(additionalPadding.toFloat(), (height - additionalPadding).toFloat(), maxValue, minValue)
        limiter.lowLimit = 0.0f + additionalPadding.toFloat()
        limiter.highLimit = (height - additionalPadding).toFloat()
        lineOffsetX = lineStrokeWidth / 2
        painter.strokeWidth = lineStrokeWidth
        textOffset = lineStrokeWidth + lineOffsetX

        lineStart = (height - additionalPadding).toFloat()
        lineEnd = converter.revConvert(sliderPos)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(
            lineOffsetX,
            additionalPadding.toFloat(),
            lineOffsetX,
            lineStart,
            painter.apply {
                alpha = 128
            }
        )
        canvas.drawLine(
            lineOffsetX,
            lineEnd,
            lineOffsetX,
            lineStart,
            painter.apply {
                alpha = 255
            }
        )
        labels?.forEach {
            canvas.drawText(
                it.key,
                textOffset,
                converter.revConvert(it.value),
                textPainter
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN -> {
                lineEnd = limiter.checkValue(event.y)
                valueChangeHandler(converter.convert(lineEnd))
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                lineEnd = limiter.checkValue(event.y)
                valueChangeHandler(converter.convert(lineEnd))
                invalidate()
            }
        }
        return true
    }
}