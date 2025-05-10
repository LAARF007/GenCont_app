package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.Cours
import com.example.gencont_app.configDB.data.Utilisateur

@Dao
interface UtilisateurDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(utilisateur: Utilisateur): Long

    @Query("SELECT * FROM Utilisateur WHERE id = :id")
    suspend fun getUtilisateurById(id: Long): Utilisateur?

    @Query("SELECT * FROM Utilisateur WHERE email = :email")
    suspend fun getUtilisateurByEmail(email: String): Utilisateur?

    @Update
    suspend fun update(utilisateur: Utilisateur)

    @Delete
    suspend fun delete(utilisateur: Utilisateur)

    @Query("SELECT * FROM Utilisateur")
    suspend fun getAllUtilisateurs(): List<Utilisateur>

    @Query("SELECT * FROM Cours WHERE utilisateurId = :utilisateurId")
    suspend fun getCoursByUtilisateur(utilisateurId: Long): List<Cours>

    @Query("DELETE FROM Utilisateur")
    suspend fun deleteAllUtilisateurs()
}