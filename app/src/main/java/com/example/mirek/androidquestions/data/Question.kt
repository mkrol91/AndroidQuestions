package com.example.mirek.androidquestions.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "questions")
data class Question @JvmOverloads constructor(
        @PrimaryKey @ColumnInfo(name = "questionId") var id: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = "content") var content: String = "",
        @ColumnInfo(name = "type") var type: String = ""
) {

    val isWrong
        get() = content.isEmpty() || type.isEmpty()
}