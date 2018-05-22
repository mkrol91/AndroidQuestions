package com.example.mirek.androidquestions.data.source

class QuestionsRepository(val questionsLocalDataSource: QuestionsDataSource) {
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