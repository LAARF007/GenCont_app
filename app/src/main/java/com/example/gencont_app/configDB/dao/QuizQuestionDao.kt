package com.example.gencont_app.configDB.dao


import androidx.room.*
import com.example.gencont_app.configDB.data.QuizQuestion

@Dao
interface QuizQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quizQuestion: QuizQuestion)

    @Transaction
    @Query("SELECT question_id FROM quiz_question WHERE quiz_id = :quizId")
    suspend fun getQuestionIdsForQuiz(quizId: Long): List<Long>

    @Query("DELETE FROM quiz_question WHERE quiz_id = :quizId AND question_id = :questionId")
    suspend fun deleteAssociation(quizId: Long, questionId: Long): Int

    @Query("DELETE FROM quiz_question WHERE quiz_id = :quizId")
    suspend fun deleteAllForQuiz(quizId: Long): Int
}