package com.example.daily.presentation.customView.utils


fun isStartTimeLessThanZero(value: Int) = value < 0
fun isStartTimeMoreOrSameEndTime(startTime: Int, endTime: Int) = startTime >= endTime
fun isEndTimeMoreThanMaxValue(endTime: Int) = endTime > MAX_VALUE_END_TIME

const val DEFAULT_VALUE_START_TIME = 1
const val DEFAULT_VALUE_END_TIME = 2
const val MAX_VALUE_END_TIME = 23
