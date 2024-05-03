package com.filters.domain.listfilter

import android.content.Context
import com.filters.R

class FilterLoader(private val context: Context) {

    fun loadFilters(): List<Filter> {
        val filtersList = mutableListOf<Filter>()

        val filtersArray = context.resources
            .getStringArray(R.array.filters_array)

        for (filterData in filtersArray) {
            val filterParts = filterData.split(";")
            if (filterParts.size == 33) {
                val typeFilter = filterParts[0]
                val filterTitle = filterParts[1]
                // Убираем @drawable/
                val imgResourceName = filterParts[2].substring(10)
                val imgResId = context.resources.getIdentifier(
                    imgResourceName,
                    "drawable",
                    context.packageName
                )
                val title: MutableList<String> = mutableListOf()
                title.addAll(filterParts.subList(3, 9))

                val error: MutableList<String> = mutableListOf()
                error.addAll(filterParts.subList(9, 15))

                val fields = mutableListOf<AllowedValues>()
                for (i in 15 until 33 step 3) {
                    val minValue = filterParts[i].toDoubleOrNull() ?: 0.0
                    val maxValue = filterParts[i + 1].toDoubleOrNull() ?: 0.0
                    val stepValue = filterParts[i + 2].toDoubleOrNull() ?: 0.0

                    fields.add(AllowedValues(minValue, maxValue, stepValue))
                }

                filtersList.add(
                    Filter(
                        typeFilter,
                        filterTitle,
                        imgResId,
                        title,
                        error,
                        fields
                    )
                )
            }
        }
        return filtersList
    }
}

fun loadFiltersFromResource(context: Context): List<Filter> {
    val filterLoader = FilterLoader(context)
    return filterLoader.loadFilters()
}


