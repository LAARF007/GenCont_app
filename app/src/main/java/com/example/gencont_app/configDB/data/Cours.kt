package com.example.gencont_app.configDB.data

import androidx.room.*

@Entity(
    tableName = "Cours",
    foreignKeys = [
        ForeignKey(
            entity = Prompt::class,
            parentColumns = ["id"],
            childColumns = ["promptId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["id"],
            childColumns = ["utilisateurId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("promptId"),
        Index("utilisateurId")
    ]
)
data class Cours(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titre: String?,
    val description: String?,
    val nombreSection: Int?,
    @ColumnInfo(name = "status_cours") val statusCours: String?,
    @ColumnInfo(name = "urlImage") val urlImage: String,
    @ColumnInfo(name = "promptId") val promptId: Long?,
    @ColumnInfo(name = "utilisateurId") val utilisateurId: Long?
)