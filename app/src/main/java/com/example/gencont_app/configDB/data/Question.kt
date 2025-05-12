package com.example.gencont_app.configDB.data

import androidx.room.*

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