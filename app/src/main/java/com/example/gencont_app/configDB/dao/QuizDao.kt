package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.Quiz

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quiz: Quiz): Long

    @Query("SELECT * FROM Quiz WHERE id = :id")
    suspend fun getQuizById(id: Long): Quiz?

    @Update
    suspend fun update(quiz: Quiz)

    @Delete
    suspend fun delete(quiz: Quiz)

    @Query("SELECT * FROM Quiz")
    suspend fun getAllQuizzes(): List<Quiz>
}