package me.jfenn.attribouter.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import me.jfenn.androidutils.dpToPx
import me.jfenn.androidutils.getThemedColor
import me.jfenn.attribouter.R
import kotlin.math.PI
import kotlin.math.min

class CircleImageView : AppCompatImageView {

    private val borderWidth = dpToPx(3f)
    private val borderDashPortion = 0.6f
    private val borderGapPortion = 0.4f

    private val borderRadius get() = min(width / 2f, height / 2f) - borderWidth
    private val borderPaint : Paint by lazy {
        Paint().apply {
            color = context.getThemedColor(R.attr.attribouter_textColorAccent)
            style = Paint.Style.STROKE
            strokeWidth = borderWidth.toFloat()
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true

            val circumference = 2 * PI * borderRadius
            val dashGapSize = circumference / 12f
            pathEffect = DashPathEffect(floatArrayOf(
                    (borderDashPortion * dashGapSize).toFloat(),
                    (borderGapPortion * dashGapSize).toFloat()
            ), 0f)
        }
    }

    private val path = Path()
    private val rect = RectF(0f, 0f, 0f, 0f)

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        // don't draw border if used as a small icon (width < 56dp)
        val inset = if (width > dpToPx(56f)) {
            canvas.drawCircle(width / 2f, height / 2f, borderRadius, borderPaint)
            borderWidth * 3f
        } else 0f

        rect.top = inset
        rect.left = inset
        rect.right = width - inset
        rect.bottom = height - inset

        val radius = (this.height / 2f) - inset
        canvas.clipPath(path.apply {
            reset()
            addRoundRect(rect, radius, radius, Path.Direction.CW)
        })

        canvas.translate(inset, inset)
        canvas.scale((width - inset*2) / width, (height - inset*2) / height)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth
        setMeasuredDimension(size, size)
    }
}