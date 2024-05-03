package com.filters.domain.listfilter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllowedValues (
    val minValue: Double,
    val maxValue: Double,
    val step:Double
) : Parcelable