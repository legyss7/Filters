package com.filters.presentation.fragments.calcuclation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filters.data.EnteredDao
import com.filters.data.EnteredData
import com.filters.domain.CalculationResult
import com.filters.domain.calculation.hfmbf.CalculationsHFMBF
import com.filters.domain.calculation.lfmbf.CalculationsLFMBF
import com.filters.domain.listfilter.AllowedValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculationsViewModel(private val enteredDao: EnteredDao) : ViewModel() {

    private var errorsFilters: List<String> = mutableListOf()
    private var allowedValues: List<AllowedValues> = mutableListOf()
    private var typeFilter = ""


    private var isValidInput: Array<Boolean> = arrayOf(false, false, false, false, false, false)
    private var inputErrors: Array<String> = arrayOf("", "", "", "", "", "")
    private var values: Array<Double?> = arrayOf(null, null, null, null, null, null)


    fun updateData(errors: List<String>, type: String, values: List<AllowedValues>) {
        errorsFilters = errors
        typeFilter = type
        allowedValues = values
    }

    private val _enteredData = MutableStateFlow<EnteredData?>(null)
    val enteredData: StateFlow<EnteredData?> = _enteredData

    private val _state = MutableStateFlow<State>(State.INPUT)
    val state = _state.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult: StateFlow<CalculationResult?> = _calculationResult

    fun getData() {
        viewModelScope.launch {
            val enteredData = enteredDao.getEnteredData(typeFilter)
            _enteredData.value = enteredData
        }
    }

    fun saveData() {
        val newEnteredData = EnteredData(
            typeFilter = typeFilter,
            valueOne = values[0] ?: 0.0,
            valueTwo = values[1] ?: 0.0,
            valueThree = values[2] ?: 0.0,
            valueFour = values[3] ?: 0.0,
            valueFive = values[4] ?: 0.0,
            valueSix = values[5] ?: 0.0
        )
        viewModelScope.launch {
            enteredDao.addEnteredData(newEnteredData)
        }
    }

    fun inputValueOne(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 0

        val errorMessage = when {
            value == null || value
                    !in allowedValues[index].minValue..allowedValues[index].maxValue
            -> errorsFilters[index]

            else -> null
        }

        checkError(errorMessage, value, index)
    }

    fun inputValueTwo(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 1

        val errorMessage = when {
            value == null || value
                    !in allowedValues[index].minValue..allowedValues[index].maxValue
                    || value.rem(allowedValues[index].step) != 0.0 -> errorsFilters[index]

            else -> null
        }

        checkError(errorMessage, value, index)
    }
    
    fun inputValueThree(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 2

        val errorMessage = when {
            value == null || value
                    !in allowedValues[index].minValue..allowedValues[index].maxValue
            -> errorsFilters[index]
            else -> null
        }

        checkError(errorMessage, value, index)
    }

    fun inputValueFour(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 3

        val errorMessage = when {
            value == null || value !in allowedValues[index].minValue..allowedValues[index].maxValue
            -> errorsFilters[index]
            else -> null
        }

        checkError(errorMessage, value, index)
    }

    fun inputValueFive(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 4

        val errorMessage = when {
            value == null || value
                    !in allowedValues[index].minValue..allowedValues[index].maxValue
            -> errorsFilters[index]

            else -> null
        }

        checkError(errorMessage, value, index)
    }

    fun inputValueSix(text: CharSequence?) {
        val value = text?.toString()?.toDoubleOrNull()
        val index = 5

        val errorMessage = when {
            value == null || value
                    !in allowedValues[index].minValue..allowedValues[index].maxValue
            -> inputErrors[index].ifEmpty { errorsFilters[index - 1] }

            else -> null
        }
        checkError(errorMessage, value, index)
    }

    private fun checkError(errorMessage: String?, value: Double?, index: Int) {
        if (errorMessage != null) {
            isValidInput[index] = false
            inputErrors[index] = errorMessage

        } else {
            isValidInput[index] = true
            inputErrors[index] = ""
            values[index] = value
        }
        _state.value = checkAllInputs()
    }

    private fun checkAllInputs(): State {
        return if (isValidInput.all { it }) {
            State.CALCULATION
        } else {
            if (inputErrors.all { it.isEmpty() }) {
                State.INPUT
            } else {
                State.ERROR(
                    inputErrors[0],
                    inputErrors[1],
                    inputErrors[2],
                    inputErrors[3],
                    inputErrors[4],
                    inputErrors[5]
                )
            }
        }
    }

    fun calculate() {

        when (typeFilter) {
            CalculationsLFMBF.checkName() -> {
                val checkC2 = CalculationsLFMBF.checkC2(
                    values[1]?.toInt()!!,
                    values[2]!!,
                    values[3]!!,
                    values[4]!!
                )
                if (values[5]!! <= checkC2) {
                    _calculationResult.value = CalculationsLFMBF.calculate(
                        values[0]!!,
                        values[1]?.toInt()!!,
                        values[2]!!,
                        values[3]!!,
                        values[4]!!,
                        values[5]!!
                    )
                    _state.value = State.SUCCESS
                } else {
                    isValidInput[5] = false
                    inputErrors[5] = errorsFilters[5] + checkC2
                    _state.value = checkAllInputs()
                }
            }

            CalculationsHFMBF.checkName() -> {
                _calculationResult.value = CalculationsHFMBF.calculate(
                    values[0]!!,
                    values[1]?.toInt()!!,
                    values[2]!!,
                    values[3]!!,
                    values[4]!!,
                    values[5]!!
                )
                _state.value = State.SUCCESS
            }

            else -> {}
        }
    }

}