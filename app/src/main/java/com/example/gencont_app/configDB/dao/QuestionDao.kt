package com.example.gencont_app.configDB.dao


import androidx.room.*
import androidx.room.Query
import com.example.gencont_app.configDB.data.Question

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(question: Question): Long

    @Query("SELECT * FROM Question WHERE id = :id")
    suspend fun getQuestionById(id: Long): Question?

    @Update
    suspend fun update(question: Question)

    @Delete
    suspend fun delete(question: Question)

    @Query("SELECT * FROM Question")
    suspend fun getAllQuestions(): List<Question>

    @Query("""
    SELECT Question.* 
    FROM Question 
    INNER JOIN quiz_question ON Question.id = quiz_question.question_id 
    WHERE quiz_question.quiz_id = :quizId
""")
    suspend fun getQuestionsForQuiz(quizId: Long): List<Question>

}