package com.example.gencont_app.configDB.data

import androidx.room.*

@Entity(
    tableName = "quiz_sections",
    primaryKeys = ["quiz_id", "section_id"],
    foreignKeys = [
        ForeignKey(
            entity = Quiz::class,
            parentColumns = ["id"],
            childColumns = ["quiz_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Section::class,
            parentColumns = ["section_id"],
            childColumns = ["section_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("section_id"),
        Index("quiz_id")
    ]
)
data class QuizSection(
    @ColumnInfo(name = "quiz_id")
    val quizId: Long,

    @ColumnInfo(name = "section_id")
    val sectionId: Long,

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String? = null
) {
    companion object {
        const val TABLE_NAME = "quiz_sections"
    }
}
