package com.filters.presentation.fragments.calcuclation

sealed class State {
    data object INPUT : State()
    data object CALCULATION : State()
    data object SUCCESS : State()
    data class ERROR(
        val errorValueOne: String,
        val errorValueTwo: String,
        val errorValueThree: String,
        val errorValueFour: String,
        val errorValueFive: String,
        val errorValueSix: String
    ) : State()
}