package com.example.gencont_app.configDB.data

import androidx.room.*

@Entity(tableName = "Cours")
data class Cours(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titre: String?,
    val description: String?,
    val nombreSection: Int?,
    @ColumnInfo(name = "status_cours") val statusCours: String?
)