package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ExpenseItemEntity
import com.example.data.model.TeamEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [ClearanceSheetEntity::class, ExpenseItemEntity::class, TeamEntity::class, UserAccountEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clearanceDao(): ClearanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "systel_clearance_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
