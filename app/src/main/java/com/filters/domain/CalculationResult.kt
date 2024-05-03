package com.filters.domain

import com.jjoe64.graphview.series.DataPoint

interface CalculationResult {
    val result: String
    val graphData: Array<DataPoint>
}