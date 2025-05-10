package com.example.gencont_app.configDB.sqlite.data

import androidx.room.*

@Entity(
    tableName = "Quiz",
    foreignKeys = [ForeignKey(
        entity = Section::class,
        parentColumns = ["section_id"],
        childColumns = ["section_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("section_id")]
)
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val ref: String?,
    val lib: String?,
    val nb_rep_correct: Int?,
    val score: Double?,

    @ColumnInfo(name = "section_id")
    val sectionId: Long
)