package com.billsAplication.data.room.billsDb

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.data.room.model.CategoryBillEntity
import com.billsAplication.data.room.model.NoteEntity

@Database(
    entities = [
        BillEntity::class,
        NoteEntity::class,
        CategoryBillEntity::class
    ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 3, to = 4),
        AutoMigration (from = 4, to = 5)
    ]
)
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
                    .build() //.fallbackToDestructiveMigration()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun destroyInstance() {
            if (INSTANCE != null) {
                if (INSTANCE!!.isOpen) {
                    INSTANCE!!.close()
                }
                INSTANCE = null
            }
        }
    }


}