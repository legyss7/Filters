package com.filters.domain.calculation.hfmbf

import com.filters.domain.CalculationResult
import com.jjoe64.graphview.series.DataPoint

data class CalculationsHFMBFResult(
    override val result: String,
    override val graphData: Array<DataPoint>
) : CalculationResult {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalculationsHFMBFResult

        if (result != other.result) return false
        if (!graphData.contentEquals(other.graphData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = result.hashCode()
        result1 = 31 * result1 + graphData.contentHashCode()
        return result1
    }
}
