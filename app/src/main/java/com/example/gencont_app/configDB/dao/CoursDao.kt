package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.Cours

@Dao
interface CoursDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cours: Cours): Long

    @Query("SELECT * FROM Cours WHERE id = :id")
    suspend fun getCoursById(id: kotlin.Long): Cours?

    @Update
    suspend fun update(cours: Cours)

    @Delete
    suspend fun delete(cours: Cours)

    @Query("SELECT * FROM Cours")
    suspend fun getAllCours(): List<Cours>

    @Query("SELECT * FROM Cours WHERE titre LIKE :titre")
    suspend fun getCoursByTitre(titre: String): List<Cours>


}