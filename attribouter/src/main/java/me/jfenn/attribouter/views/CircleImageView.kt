package me.jfenn.attribouter.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class CircleImageView : AppCompatImageView {

    private val path = Path()
    private val rect = RectF(0f, 0f, 0f, 0f)

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        rect.apply {
            right = this@CircleImageView.width.toFloat()
            bottom = this@CircleImageView.height.toFloat()
        }

        val radius = this.height / 2f
        canvas.clipPath(path.apply {
            reset()
            addRoundRect(rect, radius, radius, Path.Direction.CW)
        })

        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth
        setMeasuredDimension(size, size)
    }
}