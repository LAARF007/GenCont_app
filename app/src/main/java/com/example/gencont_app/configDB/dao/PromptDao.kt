package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.Prompt

@Dao
interface PromptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prompt: Prompt): Long

    @Query("SELECT * FROM Prompt WHERE id = :id")
    suspend fun getPromptById(id: Long): Prompt?

    @Query("SELECT * FROM Prompt WHERE utilisateurId = :utilisateurId")
    suspend fun getPromptsByUtilisateur(utilisateurId: Long): List<Prompt>

    @Update
    suspend fun update(prompt: Prompt)

    @Delete
    suspend fun delete(prompt: Prompt)

    @Query("SELECT * FROM Prompt")
    suspend fun getAllPrompts(): List<Prompt>
}