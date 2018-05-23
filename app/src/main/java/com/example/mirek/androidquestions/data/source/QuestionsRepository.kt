package com.example.mirek.androidquestions.data.source

import com.example.mirek.androidquestions.data.Question

class QuestionsRepository(val questionsLocalDataSource: QuestionsDataSource) {

    fun saveQuestion(question: Question) {
        //TODO: Do in memory cache update to keep the app UI up to date
        questionsLocalDataSource.saveQuestion(question)
    }

    companion object {
        private var INSTANCE: QuestionsRepository? = null

        @JvmStatic
        fun getInstance(questionsLocalDataSource: QuestionsDataSource) = INSTANCE
                ?: synchronized(QuestionsRepository::class.java) {
                    INSTANCE ?: QuestionsRepository(questionsLocalDataSource)
                            .also { INSTANCE = it }
                }
    }
}