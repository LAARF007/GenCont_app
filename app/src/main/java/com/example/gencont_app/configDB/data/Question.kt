package com.example.gencont_app.configDB.data

import androidx.room.*
import androidx.room.Entity

@Entity(tableName = "Question")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ref: String?,
    val libelle: String?,
    val status_question: String?
)