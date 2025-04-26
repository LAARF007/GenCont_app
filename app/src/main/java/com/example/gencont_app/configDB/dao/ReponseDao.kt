package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.Reponse

@Dao
interface ReponseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reponse: Reponse): Long

    @Query("SELECT * FROM Reponse WHERE id = :id")
    suspend fun getReponseById(id: Long): Reponse?

    @Query("SELECT * FROM Reponse WHERE questionId = :questionId")
    suspend fun getReponsesByQuestion(questionId: Long): List<Reponse>

    @Update
    suspend fun update(reponse: Reponse)

    @Delete
    suspend fun delete(reponse: Reponse)

    @Query("SELECT * FROM Reponse")
    suspend fun getAllReponses(): List<Reponse>
}