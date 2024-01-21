package com.example.daily.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
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
        listenersResetErrorInputLayouts()
    }

    private fun listenersResetErrorInputLayouts() {
        binding.textInputEditTextTitle.addTextChangedListener {
            binding.textInputLayoutTitle.error = null
        }
        binding.textInputEditTextDetails.addTextChangedListener {
            binding.textInputLayoutDetails.error = null
        }
    }

    private fun listenerEditTextDateToGetTime() {
        with(binding.textInputEditTextDate) {
            doOnTextChanged { text, start, before, _ ->
                val textToString = text.toString()
                val startTask = textToString.substringBefore(SEPARATOR)

                binding.textInputLayoutDate.error = null

                when (start) {
                    in 0..2 -> {
                        if (!textToString.contains(SEPARATOR) && regex.matches(startTask)) {
                            this.text?.append(SEPARATOR)
                        } else if (textToString.contains(SEPARATOR) && !regex.matches(startTask)) {
                            val result = textToString.substring(start, start)
                            setText(result)
                            setSelection(result.length)
                        }
                    }
                    3 -> if(textToString.length == 4 && before > 0) this.text = null

                    4 -> {
                        var startTaskToInt = startTask.toInt()
                        var endTaskToInt = textToString.substringAfter(SEPARATOR).toInt()

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

                if(checkDataFromEditTextsForCorrectness()) return@setOnClickListener

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

    private fun checkDataFromEditTextsForCorrectness(): Boolean {
        var error = false
        with(binding) {
            val title = textInputEditTextTitle.text.toString()
            val description = textInputEditTextDetails.text.toString()
            val time = textInputEditTextDate.text.toString()

            if(title.isBlank()) {
                textInputLayoutTitle.error = REPORT_ERROR
                error = true
            }
            if(description.isBlank()) {
                textInputLayoutDetails.error = REPORT_ERROR
                error = true
            }
            if(time.length != 5) {
                textInputLayoutDate.error = REPORT_ERROR
                error = true
            }
        }
        return error
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
        private const val REPORT_ERROR = " "
    }

    enum class Mode {
        READING, ADDING, UNSPECIFIED
    }
}