package com.example.mirek.androidquestions.data

import android.content.Context
import com.example.mirek.androidquestions.data.source.QuestionsLocalDataSource
import com.example.mirek.androidquestions.data.source.QuestionsRepository

object Injection {
    fun provideQuestionsRepository(context: Context): QuestionsRepository {
        return QuestionsRepository.getInstance(QuestionsLocalDataSource.getInstance())
    }
}