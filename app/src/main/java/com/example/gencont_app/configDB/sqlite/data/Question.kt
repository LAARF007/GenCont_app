package com.example.gencont_app.configDB.sqlite.data

import androidx.room.*
import androidx.room.Entity

@Entity(
    tableName = "Question",
    foreignKeys = [ForeignKey(
        entity = Quiz::class,
        parentColumns = ["id"],
        childColumns = ["quiz_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("quiz_id")]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val ref: String?,
    val libelle: String?,
    val status_question: String?,

    @ColumnInfo(name = "quiz_id")
    val quizId: Long
)