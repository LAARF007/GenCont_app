package com.example.gencont_app.configDB.dao

import androidx.room.*
import com.example.gencont_app.configDB.data.Section

@Dao
interface SectionDao {
    // INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sections: List<Section>): List<Long>

    // SELECT
    @Query("SELECT * FROM sections WHERE section_id = :id LIMIT 1")
    suspend fun getById(id: Long): Section?

    @Query("SELECT * FROM sections WHERE cours_id = :coursId ORDER BY numero_order ASC")
    suspend fun getSectionsForCours(coursId: Long): List<Section>

    @Transaction
    @Query("SELECT * FROM sections WHERE cours_id = :coursId ORDER BY numero_order ASC")
    suspend fun getByCoursId(coursId: Long): List<Section>

    @Query("SELECT COUNT(*) FROM sections WHERE cours_id = :coursId")
    suspend fun countByCoursId(coursId: Long): Int

    // UPDATE
    @Update
    suspend fun update(section: Section): Int

    @Query("UPDATE sections SET numero_order = :newOrder WHERE section_id = :sectionId")
    suspend fun updateOrder(sectionId: Long, newOrder: Int): Int

    // DELETE
    @Delete
    suspend fun delete(section: Section): Int

    @Query("DELETE FROM sections WHERE section_id = :sectionId")
    suspend fun deleteById(sectionId: Long): Int

    @Query("DELETE FROM sections WHERE cours_id = :coursId")
    suspend fun deleteByCoursId(coursId: Long): Int
}