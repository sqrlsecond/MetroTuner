package ru.makarovda.metrotuner.ui.tuner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.round


class HorizontalPitchMeter : View {

    private var painter: Paint? = null
    private var elementsDistance: Float = 20.0f
    private val elements = Array<Boolean>(11){false}

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        painter = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 20.0f
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val elementsDistance = width.toFloat() / 12.0f
        val contentHeight = height.toFloat() * 0.4f
        val startY = height / 2 - contentHeight / 2.0f

        painter!!.color = 0xFF000000.toInt()
        for (i in 0..3){
            if(elements[i]){
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight).toFloat(),
                    painter!!
                )
            }
            else {
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight / 2.0f).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight / 2.0f).toFloat(),
                    painter!!
                )
            }
        }

        painter!!.color = 0xFF1565C0.toInt()
        for (i in 4..6){
            if(elements[i]){
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight).toFloat(),
                    painter!!
                )
            }
            else {
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight / 2.0f).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight / 2.0f).toFloat(),
                    painter!!
                )
            }
        }

        painter!!.color = 0xFF000000.toInt()
        for (i in 7..10){
            if(elements[i]){
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight).toFloat(),
                    painter!!
                )
            }
            else {
                canvas.drawLine(
                    elementsDistance * (i + 1),
                    (height / 2 - contentHeight / 2.0f).toFloat(),
                    elementsDistance * (i + 1),
                    (height / 2 + contentHeight / 2.0f).toFloat(),
                    painter!!
                )
            }
        }
    }

    fun setDeviation(deviation: Int){
        if ((deviation < -50) || (deviation > 50)){
            return
        }
        elements.fill(false, 0, 11)
        val index = round(((deviation.toFloat() + 50.0f) / 100.0f * 10.0f)).toInt()
        elements[index] = true
        invalidate()
    }

}