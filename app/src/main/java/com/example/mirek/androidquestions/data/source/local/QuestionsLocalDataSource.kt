package com.example.mirek.androidquestions.data.source.local

import com.example.mirek.androidquestions.data.Question
import com.example.mirek.androidquestions.util.AppExecutors

class QuestionsLocalDataSource private constructor(val appExecutors: AppExecutors, val questionsDao: QuestionsDao) : QuestionsDataSource {

    override fun saveQuestion(question: Question) {
        appExecutors.diskIO.execute { questionsDao.insert(question) }
    }

    companion object {
        private var INSTANCE: QuestionsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, questionsDao: QuestionsDao): QuestionsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = QuestionsLocalDataSource(appExecutors, questionsDao)
            }
            return INSTANCE!!
        }
    }
}