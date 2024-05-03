package com.filters.domain.listfilter


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    val typeFilter: String,
    val titleFilter: String,
    val imgSchema: Int,
    val label: List<String>,
    val errors: List<String>,
    val fields: List<AllowedValues>
) : Parcelable





