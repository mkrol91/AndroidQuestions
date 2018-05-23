package com.example.mirek.androidquestions.data.source

import com.example.mirek.androidquestions.data.Question

interface QuestionsDataSource {
    fun saveQuestion(question: Question)
}