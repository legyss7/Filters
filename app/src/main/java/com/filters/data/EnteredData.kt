package com.filters.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class EnteredData(
    @PrimaryKey
    @ColumnInfo(name = "typeFilter")
    val typeFilter: String,
    @ColumnInfo(name = "valueOne")
    val valueOne: Double,
    @ColumnInfo(name = "valueTwo")
    val valueTwo: Double,
    @ColumnInfo(name = "valueThree")
    val valueThree: Double,
    @ColumnInfo(name = "valueFour")
    val valueFour: Double,
    @ColumnInfo(name = "valueFive")
    val valueFive: Double,
    @ColumnInfo(name = "valueSix")
    val valueSix: Double
)