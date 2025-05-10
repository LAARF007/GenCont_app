package com.example.gencont_app.configDB.sqlite.data

import androidx.room.*

@Entity(
    tableName = "sections",
    foreignKeys = [ForeignKey(
        entity = Cours::class,
        parentColumns = ["id"],
        childColumns = ["cours_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("cours_id")]
)
data class Section(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "section_id")
    val id: Long = 0,

    @ColumnInfo(name = "titre")
    val titre: String,

    @ColumnInfo(name = "urlImage")
    val urlImage: String,

    @ColumnInfo(name = "urlVideo")
    val urlVideo: String,

    @ColumnInfo(name = "contenu")
    val contenu: String,

    @ColumnInfo(name = "exemple")
    val exemple: String,

    @ColumnInfo(name = "numero_order")
    val numeroOrder: Int,

    @ColumnInfo(name = "cours_id")
    val coursId: Long,

    @ColumnInfo(name = "date_creation", defaultValue = "CURRENT_TIMESTAMP")
    val dateCreation: String? = null
) {
    companion object {
        const val TABLE_NAME = "sections"
    }
}
