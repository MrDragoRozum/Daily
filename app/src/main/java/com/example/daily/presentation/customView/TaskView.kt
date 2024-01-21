package com.example.daily.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.example.daily.R
import com.example.daily.databinding.TaskViewBinding
import com.example.daily.presentation.customView.utils.*
import kotlin.math.roundToInt

typealias OnClickTaskListener = (TaskView) -> Unit

class TaskView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(
    context,
    attributeSet,
    defStyleAttrs,
    defStyleRes
) {
    private val binding: TaskViewBinding

    private val borderPaint: Paint

    var title: String? = null
        set(value) {
            field = value
            binding.textViewTitle.text = value
        }

    var startTime: Int = DEFAULT_VALUE_START_TIME
        set(value) {
            if (isStartTimeLessThanZero(value)) return
            field = value
        }
    var endTime: Int = DEFAULT_VALUE_END_TIME
        set(value) {
            if (isStartTimeMoreOrSameEndTime(startTime, value)) {
                startTime = DEFAULT_VALUE_START_TIME
                installTime()
                return
            }
            if (isEndTimeMoreThanMaxValue(value)) {
                field = MAX_VALUE_END_TIME
                installTime()
                return
            }
            field = value
            installTime()
        }

    init {
        inflate(context, R.layout.task_view, this)
        binding = TaskViewBinding.bind(this)

        orientation = VERTICAL
        background = AppCompatResources.getDrawable(context, R.color.blue)

        borderPaint = Paint().apply {
            color = context.getColor(R.color.black)
            style = Paint.Style.STROKE
            strokeWidth = 2f.toPX()
        }
    }

    fun getEventTime() = startTime to endTime

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedHeight = (endTime - startTime) * SIZE_TASK_ON_HOUR.toPX()
        val calculateWidth = MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(calculateWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(calculatedHeight.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
    }

    private fun Float.toPX() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )

    private fun installTime() {
        binding.textViewTime.text = context.getString(
            R.string.time_taskView,
            startTime,
            endTime
        )
    }

    companion object {
        private const val SIZE_TASK_ON_HOUR = 50f
    }
}