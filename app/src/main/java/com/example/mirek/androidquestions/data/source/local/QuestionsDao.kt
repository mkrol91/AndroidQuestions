package com.example.mirek.androidquestions.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.example.mirek.androidquestions.data.Question

@Dao
interface QuestionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)
}