package com.palacios.daily_notes.data.repo

import androidx.room.*
import com.palacios.daily_notes.data.entity.Note
import kotlinx.coroutines.flow.Flow
import com.palacios.daily_notes.data.dao.NoteDao


class NoteRepo(
    private val noteDao : NoteDao
) {
    val allNotes : Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note : Note){
        noteDao.insert(note)
    }

    suspend fun update(note: Note){
        noteDao.update(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    suspend fun getNoteById(id : Int) : Note?{
        return noteDao.getNoteById(id)
    }

     fun getNotesByStatus(isCompleted : Boolean) : Flow<List<Note>>{
        return noteDao.getNotesByStatus(isCompleted)
    }

     fun searchNotes(searchQuery : String) : Flow<List<Note>>{
        return noteDao.searchNotes(searchQuery)
    }

     fun getAllCategories() : Flow<List<String>>{
        return noteDao.getAllCategories()
    }

}