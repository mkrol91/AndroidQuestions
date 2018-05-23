package com.example.mirek.androidquestions.data

import android.content.Context
import com.example.mirek.androidquestions.data.source.AndroidQuestionsDatabase
import com.example.mirek.androidquestions.data.source.QuestionsLocalDataSource
import com.example.mirek.androidquestions.data.source.QuestionsRepository

object Injection {
    fun provideQuestionsRepository(context: Context): QuestionsRepository {
        val database = AndroidQuestionsDatabase.getInstance()
        return QuestionsRepository.getInstance(QuestionsLocalDataSource.getInstance())
    }

//    fun getInstance(context: Context): AndroidQuestionsDatabase{
//
//    }
}