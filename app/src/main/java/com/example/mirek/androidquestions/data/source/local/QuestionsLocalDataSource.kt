package com.example.mirek.androidquestions.data.source.local

import com.example.mirek.androidquestions.data.Question
import com.example.mirek.androidquestions.util.AppExecutors

class QuestionsLocalDataSource private constructor(appExecutors: AppExecutors) : QuestionsDataSource {
    override fun saveQuestion(question: Question) {
    }

    companion object {
        private var INSTANCE: QuestionsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors): QuestionsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = QuestionsLocalDataSource(appExecutors)
            }
            return INSTANCE!!
        }
    }
}