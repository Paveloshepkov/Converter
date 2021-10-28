package com.example.converter.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ValuteDao {
    @Query("select * from DatabaseValute")
    fun getValute(): LiveData<List<DatabaseValute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(valute: List<DatabaseValute>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(valute: DatabaseValute)
}

@Database(entities = [DatabaseValute::class], version = 1, exportSchema = false)
abstract class ValuteDatabase : RoomDatabase() {

    abstract fun valuteDao(): ValuteDao

    companion object {
        @Volatile
        private var INSTANCE: ValuteDatabase? = null

        fun getDatabase(context: Context): ValuteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ValuteDatabase::class.java,
                    "Valute"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}