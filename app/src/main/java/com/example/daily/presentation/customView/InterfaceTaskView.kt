package com.example.daily.presentation.customView

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.example.daily.R
import com.example.daily.databinding.InterfaceOfTaskBinding
import com.example.daily.domain.models.Task

typealias OnClickAddingButtonTaskListener = ((task: Task) -> Unit)

class InterfaceTaskView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(
    context,
    attributeSet,
    defStyleAttrs,
    defStyleRes
) {

    private val binding: InterfaceOfTaskBinding
    private var listener: OnClickAddingButtonTaskListener? = null

    var task: Task? = null

    private var startTime = DEFAULT_VALUE_START_TIME
    private var endTime = DEFAULT_VALUE_END_TIME
    private val regex = Regex("""\d{2}""")

    init {
        inflate(context, R.layout.interface_of_task, this)
        binding = InterfaceOfTaskBinding.bind(this)
        listener()
    }

    fun setMode(mode: Mode = Mode.UNSPECIFIED) {
        when (mode) {
            Mode.READING -> readingMode()
            Mode.ADDING -> writingMode()
            Mode.UNSPECIFIED -> error("bruh")
        }
    }

    fun setListener(listener: OnClickAddingButtonTaskListener) {
        this.listener = listener
    }

    private fun listener() {
        listenerButtonForCreatingTask()
        listenerEditTextDateToGetTime()
    }

    private fun listenerEditTextDateToGetTime() {
        with(binding.textInputEditTextDate) {
            doOnTextChanged { text, start, before, _ ->
                var textToString = text.toString()
                val startTask = textToString.substringBefore(SEPARATOR)

                when (start) {
                    in 0..2 -> {
                        if (canAddingSeparator(textToString, startTask)) {
                            this.text?.append(SEPARATOR)
                        } else if (!canAddingSeparator(textToString, startTask)) {
                            textToString = textToString.substring(start, start)
                            setText(textToString)
                            setSelection(textToString.length)
                        }
                    }

                    4 -> {
                        var startTaskToInt = startTask.toInt()
                        var endTaskToInt = textToString.substringBefore(SEPARATOR).toInt()

                        val pair = checkTimeForCorrect(startTaskToInt, endTaskToInt)
                        endTaskToInt = pair.first
                        startTaskToInt = pair.second

                        if (before == 0) {
                            setText(
                                context.getString(
                                    R.string.time_task_in_interfaceTaskView,
                                    startTaskToInt,
                                    endTaskToInt
                                )
                            )
                            setSelection(textToString.length)
                            startTime = startTaskToInt
                            endTime = endTaskToInt
                        } else {
                            this.text = null
                        }
                    }
                }
            }
        }
    }

    private fun listenerButtonForCreatingTask() {
        with(binding) {
            buttonAddingTask.setOnClickListener {
                Task(
                    dateStart = startTime.toLong(),
                    dateFinish = endTime.toLong(),
                    name = textInputEditTextTitle.text.toString(),
                    description = textInputEditTextDetails.text.toString()
                ).also {
                    listener?.invoke(it)
                }
            }
        }
    }

    private fun checkTimeForCorrect(
        startTaskToInt: Int,
        endTaskToInt: Int
    ): Pair<Int, Int> {
        var newStartTaskToInt = startTaskToInt
        var newEndTaskToInt = endTaskToInt
        if (isStartTimeMoreThanZero(newStartTaskToInt)) {
            newStartTaskToInt = DEFAULT_VALUE_START_TIME
        }

        if (isStartTimeMoreOrSameEndTime(newStartTaskToInt, newEndTaskToInt)) {
            newStartTaskToInt = DEFAULT_VALUE_START_TIME
            newEndTaskToInt = DEFAULT_VALUE_END_TIME
        }

        if (isEndTimeMoreThanMaxValue(newEndTaskToInt)) {
            newEndTaskToInt = MAX_VALUE_END_TIME
        }
        return Pair(newEndTaskToInt, newStartTaskToInt)
    }

    private fun canAddingSeparator(
        textToString: String,
        startTask: String
    ) = !textToString.contains(SEPARATOR) && regex.matches(startTask)

    private fun readingMode() {
        with(binding) {
            task?.let {
                buttonAddingTask.visibility = INVISIBLE
                textInputEditTextDate.isEnabled = false
                textInputEditTextDetails.isEnabled = false
                textInputEditTextTitle.isEnabled = false

                val time = context.getString(R.string.time_taskView, it.dateStart, it.dateFinish)
                textInputEditTextDate.setText(time)
                textInputEditTextDetails.setText(it.description)
                textInputEditTextTitle.setText(it.name)
            }
        }
    }

    private fun writingMode() {
        with(binding) {
            buttonAddingTask.visibility = VISIBLE
            textInputEditTextDate.isEnabled = true
            textInputEditTextDetails.isEnabled = true
            textInputEditTextTitle.isEnabled = true
        }
    }

    companion object {
        private const val SEPARATOR = "-"
    }

    enum class Mode {
        READING, ADDING, UNSPECIFIED
    }
}