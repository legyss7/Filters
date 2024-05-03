package com.filters.domain.calculation.lfmbf

import com.filters.domain.CalculationResult
import com.jjoe64.graphview.series.DataPoint
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class CalculationsLFMBF {
    companion object {
        // Уровень напряжения на частоте среза звена второго порядка -3 дБ
        // при коэффициенте усиления единица
        private const val VOLTAGE_CONST: Double = 0.70796

        //Кол-во точек на графике
        private const val SIZE_POINT: Int = 1000

        // Размер АЧХ, одна декада от частоты среза
        private const val SIZE_AMPLITUDE_FREQUENCY_RESPONSE: Double = 10.0
        private const val TWO_PI = 2.0 * Math.PI

        // Метод для выполнения проверки устойчивости звена второго порядка фильтра
        fun checkC2(
            filterOrder: Int,
            qualityFactor: Double,
            ampFactor: Double,
            c1: Double
        ): Double {
            val ampK = ampValue(filterOrder, ampFactor)
            return c1 / (4.0 * qualityFactor.pow(2.0) * (1.0 + ampK))
        }

        fun checkName(): String {
            return "lf_mbf"
        }

        // Метод для выполнения расчетов и возвращения результата
        fun calculate(
            cutoffFrequency: Double,
            filterOrder: Int,
            qualityFactor: Double,
            ampFactor: Double,
            c1: Double,
            c2: Double
        ): CalculationResult {

            val c1F = c1 * 1E-9 // F
            val c2F = c2 * 1E-9 // F
            val cutoffFrequencyHz = cutoffFrequency * 1E+3 // Hz
            val ampK = ampValue(filterOrder, ampFactor)

            val factorB =
                calculateFactorB(cutoffFrequencyHz, filterOrder, qualityFactor);
            val factorA = calculateFactorA(factorB, qualityFactor);

            val r3 = calculateR3(ampK, c1F, c2F, factorA, factorB)
            val r2 = calculateR2(factorA, c2F, r3, ampK)
            val r1 = calculateR1(r3, ampK)

            val graphData = calculateGraphData(
                factorA,
                factorB,
                cutoffFrequencyHz,
                filterOrder,
                ampK
            )

            // Возвращаем результаты в виде объекта CalculationsLFMBFResult
            return CalculationsLFMBFResult(
                "R1 = ${round(r1)} Ом \n"
                        + "R2 = ${round(r2)} Ом \n"
                        + "R3 = ${round(r3)} Ом",
                graphData
            )
        }

        private fun round(value: Double): Double {
            return Math.round(value * 10.0) / 10.0
        }

        private fun voltageValue(filterOrder: Int): Double {
            return when (filterOrder) {
                2 -> VOLTAGE_CONST
                4 -> VOLTAGE_CONST.pow(1.0 / 2.0)
                6 -> VOLTAGE_CONST.pow(1.0 / 3.0)
                8 -> VOLTAGE_CONST.pow(1.0 / 4.0)
                10 -> VOLTAGE_CONST.pow(1.0 / 5.0)
                else -> 0.0
            }
        }

        private fun ampValue(filterOrder: Int, ampFactor: Double): Double {
            return when (filterOrder) {
                2 -> ampFactor
                4 -> ampFactor.pow(1.0 / 2.0)
                6 -> ampFactor.pow(1.0 / 3.0)
                8 -> ampFactor.pow(1.0 / 4.0)
                10 -> ampFactor.pow(1.0 / 5.0)
                else -> 0.0
            }
        }

        private fun calculateFactorA(factorB: Double, qualityFactor: Double): Double {
            return sqrt(factorB) / qualityFactor
        }

        private fun calculateFactorB(
            cutoffFrequency: Double, filterOrder: Int,
            qualityFactor: Double
        ): Double {

            val uValue = voltageValue(filterOrder)
            val w = TWO_PI * cutoffFrequency
            val tempA = w.pow(2.0)
            val tempB = 1.0 / qualityFactor.pow(2.0) - 2.0
            val tempC = 1.0 - (1.0 / uValue).pow(2.0)

            return (sqrt(tempB.pow(2.0) - 4.0 * tempC) - tempB) / (2.0 * tempA);
        }

        private fun calculateR1(r3: Double, ampFactor: Double): Double {
            return r3 / ampFactor
        }

        private fun calculateR2(
            factorA: Double, c2: Double, r3: Double,
            ampFactor: Double
        ): Double {
            return (factorA / c2 - r3) / (1.0 + ampFactor)
        }

        private fun calculateR3(
            ampFactor: Double, c1: Double, c2: Double,
            factorA: Double, factorB: Double
        ): Double {
            val tempA = c1 * c2
            val tempB = factorA * c1
            val tempC = factorB * (1 + ampFactor)
            return (tempB - sqrt(tempB.pow(2.0) - 4 * tempA * tempC)) / (2 * tempA)
        }

        private fun calculateGraphData(
            factorA: Double,
            factorB: Double,
            cutoffFrequency: Double,
            filterOrder: Int,
            ampFactor: Double
        ): Array<DataPoint> {
            val dataPoints = mutableListOf<DataPoint>()
            var x = 0.0
            var y = 0.0
            val frequencyK =
                SIZE_AMPLITUDE_FREQUENCY_RESPONSE * TWO_PI * cutoffFrequency / SIZE_POINT

            for (i in 0..SIZE_POINT) {
                x = i.toDouble() * frequencyK
                y = 20.0 * log10(
                    ampFactor / sqrt(
                        (1.0 - x.pow(2.0) * factorB).pow(2.0) + (x * factorA).pow(2.0)
                    )
                )
                y += (filterOrder / 2 - 1) * y
                x /= TWO_PI * 1000.0 // kHz
                dataPoints.add(DataPoint(x, y))
            }
            return dataPoints.toTypedArray()
        }

    }
}
