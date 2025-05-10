package com.example.gencont_app.configDB.sqlite.data

import androidx.room.*

@Entity(
    tableName = "Reponse",
    foreignKeys = [ForeignKey(entity = Question::class, parentColumns = ["id"], childColumns = ["questionId"])],
    indices = [Index("questionId")]
)
data class Reponse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ref: String?,
    val lib: String?,
    val status: String?,
    @ColumnInfo(name = "questionId") val questionId: Long?
)