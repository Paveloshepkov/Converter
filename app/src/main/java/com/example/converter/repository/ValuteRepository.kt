package com.example.converter.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.converter.database.DatabaseValute
import com.example.converter.database.ValuteDatabase
import com.example.converter.database.asDomainModel
import com.example.converter.domain.Valute
import com.example.converter.network.ValuteNetwork
import com.example.converter.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ValuteRepository(private val database: ValuteDatabase) {

    val valute: LiveData<List<Valute>> =
        Transformations.map(database.valuteDao().getValute()) {
            it.asDomainModel()
        }

    suspend fun refreshValute() {
        withContext(Dispatchers.IO) {
            val valCursList = ValuteNetwork.getValCurs.getValCurs()
            database.valuteDao().insertAll(valCursList.asDatabaseModel())
        }
    }

    suspend fun insertValute(valute: DatabaseValute) {
        withContext(Dispatchers.IO) {
            database.valuteDao().insert(valute)
        }
    }
}