package com.example.gencont_app.configDB.dao

import androidx.room.*
import com.example.gencont_app.configDB.data.QuizSection

@Dao
interface QuizSectionDao {
    // INSERT
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quizSection: QuizSection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quizSections: List<QuizSection>)

    // SELECT
    @Query("SELECT section_id FROM quiz_sections WHERE quiz_id = :quizId")
    suspend fun getSectionIdsForQuiz(quizId: Long): List<Long>

    @Query("SELECT quiz_id FROM quiz_sections WHERE section_id = :sectionId")
    suspend fun getQuizIdsForSection(sectionId: Long): List<Long>

    @Transaction
    @Query("SELECT * FROM quiz_sections WHERE quiz_id = :quizId AND section_id = :sectionId LIMIT 1")
    suspend fun getAssociation(quizId: Long, sectionId: Long): QuizSection?

    // DELETE
    @Delete
    suspend fun delete(quizSection: QuizSection): Int

    @Query("DELETE FROM quiz_sections WHERE quiz_id = :quizId AND section_id = :sectionId")
    suspend fun deleteAssociation(quizId: Long, sectionId: Long): Int

    @Query("DELETE FROM quiz_sections WHERE quiz_id = :quizId")
    suspend fun deleteAllForQuiz(quizId: Long): Int

    @Query("DELETE FROM quiz_sections WHERE section_id = :sectionId")
    suspend fun deleteAllForSection(sectionId: Long): Int
}