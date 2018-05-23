package com.example.mirek.androidquestions.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.mirek.androidquestions.data.Question

@Database(entities = [Question::class], version = 1)
abstract class AndroidQuestionsDatabase : RoomDatabase() {

    abstract fun questionsDao(): QuestionsDao

    companion object {

        private var INSTANCE: AndroidQuestionsDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): AndroidQuestionsDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.applicationContext,
                                    AndroidQuestionsDatabase::class.java, "Questions.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }
}