package com.filters.domain.calculation.hfmbf

import com.filters.domain.CalculationResult
import com.filters.domain.calculation.lfmbf.CalculationsLFMBF
import com.jjoe64.graphview.series.DataPoint
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class CalculationsHFMBF {
    companion object {
        // Уровень напряжения на частоте среза звена второго порядка -3 дБ
        // при коэффициенте усиления единица
        private const val VOLTAGE_CONST: Double = 0.70796

        //Кол-во точек на графике
        private const val SIZE_POINT: Int = 1000

        // Размер АЧХ
        private const val MAX_AMPLITUDE_FREQUENCY_RESPONSE: Double = 1.1
        private const val TWO_PI = 2.0 * Math.PI

        fun checkName(): String {
            return "hf_mbf"
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

            val c3F = calculateC3(c1F, ampK)
            val c3 = c3F * 1E+9 //nF
            val r1 = calculateR1(c1F, c2F, c3F, factorA, factorB)
            val r2 = calculateR2(c1F, c2F, c3F, factorA)

            val graphData = calculateGraphData(
                factorA,
                factorB,
                cutoffFrequencyHz,
                filterOrder,
                ampK
            )

            // Возвращаем результаты в виде объекта CalculationsLFMBFResult
            return CalculationsHFMBFResult(
                "C3 = ${round(c3)} нФ \n"
                        + "R1 = ${round(r1)} Ом \n"
                        + "R2 = ${round(r2)} Ом",
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
            val tempA = 1.0 / w.pow(2.0)
            val tempB = 1.0 / qualityFactor.pow(2.0) - 2.0
            val tempC = 1.0 - (1.0 / uValue).pow(2.0)

            return (-tempB + sqrt(tempB.pow(2.0) - 4.0 * tempC)) / (2.0 * tempA);
        }

        private fun calculateC3(c1: Double, ampFactor: Double): Double {
            return c1 / ampFactor
        }

        private fun calculateR1(
            c1: Double, c2: Double, c3: Double,
            factorA: Double, factorB: Double
        ): Double {
            return factorA / (factorB * (c1 + c2 + c3))
        }

        private fun calculateR2(
            c1: Double, c2: Double, c3: Double,
            factorA: Double
        ): Double {
            return (c1 + c2 + c3) / (factorA * c2 * c3)
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
                MAX_AMPLITUDE_FREQUENCY_RESPONSE * TWO_PI * cutoffFrequency / SIZE_POINT

            for (i in 1..SIZE_POINT) {
                x = i.toDouble() * frequencyK

                y = 20.0 * log10(
                    ampFactor / sqrt(
                        (1.0 - (1.0 / x).pow(2.0) * factorB).pow(2.0)
                                + ((1.0 / x) * factorA).pow(2.0)
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