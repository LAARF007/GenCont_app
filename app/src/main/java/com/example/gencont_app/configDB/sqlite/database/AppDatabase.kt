package com.example.gencont_app.configDB.sqlite.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gencont_app.configDB.dao.*
import com.example.gencont_app.configDB.data.*
import com.example.gencont_app.configDB.sqlite.dao.CoursDao
import com.example.gencont_app.configDB.sqlite.dao.PromptDao
import com.example.gencont_app.configDB.sqlite.dao.QuestionDao
import com.example.gencont_app.configDB.sqlite.dao.QuizDao
import com.example.gencont_app.configDB.sqlite.dao.ReponseDao
import com.example.gencont_app.configDB.sqlite.dao.SectionDao
import com.example.gencont_app.configDB.sqlite.dao.UtilisateurDao
import com.example.gencont_app.configDB.sqlite.data.Converters
import com.example.gencont_app.configDB.sqlite.data.Cours
import com.example.gencont_app.configDB.sqlite.data.Prompt
import com.example.gencont_app.configDB.sqlite.data.Question
import com.example.gencont_app.configDB.sqlite.data.Quiz
import com.example.gencont_app.configDB.sqlite.data.Reponse
import com.example.gencont_app.configDB.sqlite.data.Section
import com.example.gencont_app.configDB.sqlite.data.Utilisateur

@Database(
    entities = [
        Cours::class,
        Prompt::class,
        Question::class,
        Quiz::class,
        Reponse::class,
        Section::class,
        Utilisateur::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coursDao(): CoursDao
    abstract fun promptDao(): PromptDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizDao(): QuizDao
    abstract fun reponseDao(): ReponseDao
    abstract fun sectionDao(): SectionDao
    abstract fun utilisateurDao(): UtilisateurDao

    companion object {
        private const val DATABASE_NAME = "gencont6_db"

        // Singleton pattern to ensure only one instance of the database is created
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // ONLY FOR TESTING! Remove in production
                    .build()
                    .also { instance = it }
            }
        }
    }
}