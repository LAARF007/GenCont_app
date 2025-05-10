package com.example.gencont_app.configDB.sqlite.data

import androidx.room.*

@Entity(
    tableName = "Prompt",
    foreignKeys = [ForeignKey(entity = Utilisateur::class, parentColumns = ["id"], childColumns = ["utilisateurId"])],
    indices = [Index("utilisateurId")]
)
data class Prompt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @TypeConverters(Converters::class)
    val Tags: List<String>?,
    val coursName: String?,
    val niveau: String?,
    val langue: String?,
    val description: String?,
    val status_user: String?,

    @ColumnInfo(name = "utilisateurId") val utilisateurId: Long?
)