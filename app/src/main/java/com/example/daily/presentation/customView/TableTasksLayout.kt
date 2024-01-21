package com.example.daily.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.example.daily.R
import kotlin.math.abs
import kotlin.math.roundToInt

class TableTasksLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(
    context,
    attributeSet,
    defStyleAttrs,
    defStyleRes
) {

    private lateinit var strokePaint: Paint
    private lateinit var textTimePaint: Paint

    private val listTaskViews = mutableListOf<TaskView>()
    private val mapSavedChanges = mutableMapOf<Pair<TaskView, TaskView>, Boolean>()
    private val listHours = MutableList(24) {
        context.getString(R.string.time_tableTaskLayout, it)
    }

    private val taskRect = Rect()
    private val textTimeRect = Rect()

    init {
        setWillNotDraw(false)
        initPaints()
    }

    private fun initPaints() {
        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.grey)
            style = Paint.Style.STROKE
            strokeWidth = 1f.toPX()
        }
        textTimePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.black)
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                SIZE_TEXT_TIME,
                resources.displayMetrics
            )
            style = Paint.Style.FILL
            textAlign = Paint.Align.LEFT
            getTextBounds(listHours[0], 0, listHours[0].length, textTimeRect)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight =
            (SIZE_CELL.toPX() * NUMBER_ROWS + paddingBottom + paddingTop + suggestedMinimumHeight)
                .toInt()
        val desiredWidth =
            resources.displayMetrics.widthPixels + paddingRight + paddingLeft + suggestedMinimumWidth

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        taskRect.apply {
            left = (textTimeRect.right + MARGIN_LINE).toInt() + paddingLeft
            right = width - paddingRight
            top = STARTING_POSITION_OF_ADDING_TASKS + paddingTop
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTimeTextsAndLines(canvas)
    }

    private fun drawTimeTextsAndLines(canvas: Canvas) {
        val sellSizeInPx = SIZE_CELL.toPX()
        for (i in 0 until NUMBER_ROWS) {
            val y = i * sellSizeInPx + taskRect.top
            val time = listHours[i]

            canvas.drawText(
                time,
                MARGIN_LEFT_SCREEN + paddingLeft,
                y + abs(textTimeRect.top) / 2,
                textTimePaint
            )

            canvas.drawLine(
                textTimeRect.right.toFloat() + MARGIN_LINE + paddingLeft,
                y,
                taskRect.right.toFloat(),
                y,
                strokePaint
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        installChildren()
    }

    private fun installChildren() {
        listTaskViews.forEachIndexed { index, child ->
            installLeftAtChild(index, child)
            installLayoutAtChild(child)
            bringSomeChildrenToFront(child)
            child.layoutParams = LayoutParams(child.width, child.height)
        }
    }

    private fun installLeftAtChild(index: Int, child: TaskView) {
        child.left = taskRect.left
        for (indexLastChild in 0 until index) {
            val lastChild = listTaskViews[indexLastChild]
            if (isSameStartTime(child, lastChild)) {
                child.left = lastChild.right + MARGIN_BETWEEN_TASKS

            }
        }
    }

    private fun installLayoutAtChild(child: TaskView) {
        with(child) {
            val sizeRight = left + taskRect.width() / getDivisorForTasks(this)

            top = calculateTopForChild(this)
            right = if (left == taskRect.left) sizeRight else sizeRight - MARGIN_BETWEEN_TASKS
            bottom = top + measuredHeight
            layout(left, top, right, bottom)
        }
    }

    private fun bringSomeChildrenToFront(childNow: TaskView) {
        val list = getCopyListTaskViewsWithoutChildNow(childNow)
        list.forEachIndexed list@{ index, anotherChild ->
            if (isSameStartTime(childNow, anotherChild)) return@list

            if (isRangeOfTimeInTask(childNow, anotherChild)) {

                if (childNow.measuredHeight > anotherChild.measuredHeight
                    && childNow.top > calculateTopForChild(anotherChild)
                ) {
                    val endTimeFromAnotherChild = anotherChild.getEventTime().second
                    val startTimeFromChildNow = childNow.getEventTime().first

                    if (endTimeFromAnotherChild - startTimeFromChildNow > 0) {
                        saveChangesAndBringChildToFont(anotherChild, childNow)
                        mapSavedChanges[childNow to anotherChild] = true
                        return@list
                    }
                } else if (childNow.measuredHeight < anotherChild.measuredHeight) {
                    if (list.size - 1 >= index + 1) {
                        val childNext = list[index + 1]

                        if (isRangeOfTimeInTask(childNow, childNext)
                            && childNow.measuredHeight < childNext.measuredHeight
                        ) return@list
                    }
                    saveChangesAndBringChildToFont(anotherChild, childNow)
                    return@list
                }
            }
        }
    }

    private fun saveChangesAndBringChildToFont(
        anotherChild: TaskView,
        childNow: TaskView
    ) {
        if (isBothChildHaveSameChanged(anotherChild, childNow)) return
        bringChildToFront(childNow)
    }

    private fun calculateTopForChild(anotherChild: TaskView) = anotherChild
        .getEventTime().first * SIZE_CELL.toPX().roundToInt() + taskRect.top

    private fun isBothChildHaveSameChanged(
        anotherChild: TaskView,
        childNow: TaskView
    ): Boolean = mapSavedChanges[anotherChild to childNow] == true

    private fun getDivisorForTasks(childNow: TaskView): Int {
        var divisor = DEFAULT_VARIABLE_DIVISOR
        getCopyListTaskViewsWithoutChildNow(childNow).forEach list@{
            if (it.getEventTime().first == childNow.getEventTime().second ||
                it.getEventTime().second == childNow.getEventTime().first
            ) return@list

            if (isSameStartTime(childNow, it)) divisor++
        }
        return divisor
    }

    private fun getCopyListTaskViewsWithoutChildNow(childNow: TaskView): List<TaskView> =
        listTaskViews.toMutableList().apply { remove(childNow) }

    private fun isSameStartTime(
        childFirst: TaskView,
        childSecond: TaskView
    ): Boolean = childFirst.getEventTime().first == childSecond.getEventTime().first

    private fun isRangeOfTimeInTask(
        firstRange: TaskView,
        secondRange: TaskView
    ): Boolean {
        val firstChild = firstRange.getEventTime()
        val secondChild = secondRange.getEventTime()

        return (firstChild.first in secondChild.first..secondChild.second) ||
                (firstChild.second in secondChild.first..secondChild.second) ||
                (secondChild.first in firstChild.first..firstChild.second) ||
                (secondChild.second in firstChild.first..firstChild.second)
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        listTaskViews.apply {
            add(child as TaskView)
            sortByDescending { it.endTime - it.startTime }
        }
    }

    override fun removeAllViews() {
        super.removeAllViews()
        listTaskViews.clear()
        mapSavedChanges.clear()
    }

    private fun Float.toPX() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )

    fun addListTaskView(list: List<TaskView>) {
        list.forEach { addView(it) }
    }

    companion object {
        private const val SIZE_CELL = 50f
        private const val NUMBER_ROWS = 24
        private const val STARTING_POSITION_OF_ADDING_TASKS = 50
        private const val MARGIN_BETWEEN_TASKS = 1
        private const val DEFAULT_VARIABLE_DIVISOR = 1

        private const val SIZE_TEXT_TIME = 16f
        private const val MARGIN_LINE = SIZE_TEXT_TIME * 2
        private const val MARGIN_LEFT_SCREEN = SIZE_TEXT_TIME
    }
}