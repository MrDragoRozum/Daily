package com.example.daily.presentation.customView.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CustomViewUtilsKtTest {

    @Test
    fun `Is start time less than zero`() {
        val valueMoreThanZero = 10
        val valueLessThanZero = -2

        val resultMore = isStartTimeLessThanZero(valueMoreThanZero)
        val resultLess = isStartTimeLessThanZero(valueLessThanZero)

        assertEquals(false, resultMore)
        assertEquals(true, resultLess)
    }

    @Test
    fun `is start time more or same end time`() {
        val endTimeLessValue = 8
        val endTimeSameValue = 12
        val value = 12
        val valueLessEndTime = 8

        val resultLess = isStartTimeMoreOrSameEndTime(value, endTimeLessValue)
        val resultSame = isStartTimeMoreOrSameEndTime(value, endTimeSameValue)
        val resultMore = isStartTimeMoreOrSameEndTime(valueLessEndTime, endTimeSameValue)

        assertEquals(true, resultLess)
        assertEquals(true, resultSame)
        assertEquals(false, resultMore)
    }

    @Test
    fun `Is end time more than maxValue`() {
        // maxValue = 23
        val valueMore = 25
        val valueLess = 21

        val resultMore = isEndTimeMoreThanMaxValue(valueMore)
        val resultLess = isEndTimeMoreThanMaxValue(valueLess)

        assertEquals(true, resultMore)
        assertEquals(false, resultLess)
    }
}