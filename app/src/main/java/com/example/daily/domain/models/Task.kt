package com.example.daily.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: Int = UNSPECIFIED_ID,
    var dateStart: Long,
    var dateFinish: Long,
    val name: String,
    val description: String
) : Parcelable

const val UNSPECIFIED_ID = 0
