package com.example.mirek.androidquestions.data

import android.content.Context
import com.example.mirek.androidquestions.data.source.local.AndroidQuestionsDatabase
import com.example.mirek.androidquestions.data.source.local.QuestionsLocalDataSource
import com.example.mirek.androidquestions.data.source.local.QuestionsRepository
import com.example.mirek.androidquestions.util.AppExecutors

object Injection {
    fun provideQuestionsRepository(context: Context): QuestionsRepository {
        val database = AndroidQuestionsDatabase.getInstance(context)
        return QuestionsRepository.getInstance(QuestionsLocalDataSource.getInstance(AppExecutors(),
                database.questionsDao()))
    }
}