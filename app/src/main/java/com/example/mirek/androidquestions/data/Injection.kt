package com.example.mirek.androidquestions.data

import android.content.Context
import com.example.mirek.androidquestions.data.source.DataRepository

object Injection {
    fun provideDataRepository(context: Context): DataRepository {
        //TODO
        return DataRepository()
    }
}