package com.example.gencont_app.configDB.data

import androidx.room.*

@Entity(tableName = "Quiz")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ref: String?,
    val lib: String?,
    val nb_rep_correct: Int?,
    val score: Double?
)