package com.example.mirek.androidquestions.data.source

import com.example.mirek.androidquestions.data.Question

class QuestionsLocalDataSource private constructor() : QuestionsDataSource {
    override fun saveQuestion(question: Question) {
    }

    companion object {
        private var INSTANCE: QuestionsLocalDataSource? = null

        @JvmStatic
        fun getInstance(): QuestionsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = QuestionsLocalDataSource()
            }
            return INSTANCE!!
        }
    }
}