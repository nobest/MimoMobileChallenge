package com.meticulous.mimomobilechallenge.tools

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class LessonComplete(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "startTimeStamp") val startTimeStamp: Long,
    @ColumnInfo(name = "endTimeStamp") val endTimeStamp: Long
)

@Dao
interface LessonCompleteDao {
    @Query("SELECT * FROM lessonComplete")
    fun getAllCompletedLessons(): List<LessonComplete>

    @Query("SELECT * FROM lessonComplete WHERE id = :lessonId")
    fun getCompletedLessonById(lessonId: Int): LessonComplete

    @Insert
    fun insertAll(vararg lessonCompletes: LessonComplete)
}

@Database(entities = arrayOf(LessonComplete::class), version = 1)
abstract class MimoChallengeDb : RoomDatabase() {
    abstract fun lessonCompleteDao(): LessonCompleteDao

    companion object {
        private const val DB_NAME = "mimoChallenge.db"
        private var INSTANCE: MimoChallengeDb? = null

        @JvmStatic
        fun getDatabase(context: Context): MimoChallengeDb =
            INSTANCE ?: synchronized(MimoChallengeDb::class) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MimoChallengeDb::class.java,
                    DB_NAME
                )
                    .enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}