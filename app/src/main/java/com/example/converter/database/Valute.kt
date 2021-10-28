package com.example.converter.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.converter.domain.Valute


@Entity
data class DatabaseValute constructor(
    @PrimaryKey
    val numCode: String,
    val charCode: String,
    val nominal: String,
    val name: String,
    val value: String
)

fun List<DatabaseValute>.asDomainModel(): List<Valute> {
    return map {
        Valute(
            numCode = it.numCode,
            charCode = it.charCode,
            nominal = it.nominal,
            name = it.name,
            value = it.value
        )
    }
}