package com.example.converter.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.converter.database.ValuteDatabase.Companion.getDatabase
import com.example.converter.repository.ValuteRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "com.example.converter.work.RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = ValuteRepository(database)

        try {
            repository.refreshValute()
            Timber.d("WorkManager: выполняется запрос на синхронизацию")
        } catch (e: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }
}