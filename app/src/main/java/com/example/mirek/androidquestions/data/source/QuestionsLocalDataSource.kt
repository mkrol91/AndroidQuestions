package com.example.mirek.androidquestions.data.source

class QuestionsLocalDataSource private constructor(

) : QuestionsDataSource {


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