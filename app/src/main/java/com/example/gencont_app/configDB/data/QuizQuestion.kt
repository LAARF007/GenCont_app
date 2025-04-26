package com.example.gencont_app.configDB.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "quiz_question",
    primaryKeys = ["quiz_id", "question_id"],
    foreignKeys = [
        ForeignKey(
            entity = Quiz::class,
            parentColumns = ["id"],
            childColumns = ["quiz_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["question_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("question_id"),
        Index("quiz_id")
    ]
)
data class QuizQuestion(
    @ColumnInfo(name = "quiz_id") val quizId: Long,
    @ColumnInfo(name = "question_id") val questionId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)