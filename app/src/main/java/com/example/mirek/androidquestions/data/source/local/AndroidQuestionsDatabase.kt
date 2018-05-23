package com.example.mirek.androidquestions.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.example.mirek.androidquestions.data.Question

@Database(entities = [Question::class], version = 1)
abstract class AndroidQuestionsDatabase : RoomDatabase() {

    companion object {

        private var INSTANCE: AndroidQuestionsDatabase? = null
        private val lock = Any()

        fun getInstance() {
            synchronized(lock) {
                if (INSTANCE == null) {

                }
            }
        }
    }
}