package com.palacios.daily_notes.data.dao

import androidx.room.*
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.data.entity.CategoryWithColor
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    @Query("SELECT * FROM notes WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getNotesByStatus(isCompleted: Boolean): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<Note>>

    @Query("SELECT DISTINCT category FROM notes ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    // Categories with their corresponding colors
    @Query("SELECT DISTINCT category, categoryColor FROM notes ORDER BY category ASC")
    fun getCategoriesWithColors(): Flow<List<CategoryWithColor>>

    @Query("""
        SELECT * FROM notes 
        WHERE CASE 
            WHEN :category = 'All' THEN 1
            ELSE CASE WHEN category = :category THEN 1 ELSE 0 END
        END
        ORDER BY createdAt DESC
    """)
    fun getNotesByCategory(category: String): Flow<List<Note>>
}