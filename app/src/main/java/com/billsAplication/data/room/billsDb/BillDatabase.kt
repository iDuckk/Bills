package com.billsAplication.data.room.billsDb

import android.content.Context
import androidx.room.Database
import androidx.room.RawQuery
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.data.room.model.BillEntity


@Database(entities = [BillEntity::class], version = 3, exportSchema = false)
abstract class BillDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BillDatabase? = null

        fun getDatabase(context: Context): BillDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillDatabase::class.java,
                    "bills_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun destroyInstance() {
            if (INSTANCE != null) {
                if (INSTANCE!!.isOpen()) {
                    INSTANCE!!.close()
                }
                INSTANCE = null
            }
        }
    }


}