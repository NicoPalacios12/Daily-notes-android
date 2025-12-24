package com.palacios.daily_notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.palacios.daily_notes.data.noteDataBase.NoteDataBase
import com.palacios.daily_notes.data.repo.NoteRepo
import androidx.lifecycle.viewModelScope
import com.palacios.daily_notes.data.entity.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch


class NoteViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val repository : NoteRepo
    val allNotes : LiveData<List<Note>>

    init{
        val noteDao = NoteDataBase.getDatabase(application).noteDao()
        repository = NoteRepo(noteDao)
        allNotes = repository.allNotes.asLiveData()
    }

    //insert note
    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    //delete note
    fun delete(note : Note) = viewModelScope.launch {
        repository.delete(note)
    }

    //update note
    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    //note by id
    suspend fun getNoteById(noteId : Int) : Note?{
        return repository.getNoteById(noteId)
    }

    //note by status
     fun getNoteByStatus(isCompleted : Boolean) : LiveData<List<Note>>{
        return repository.getNotesByStatus(isCompleted).asLiveData()
    }

    //search notes
    fun searchNote(searchQuery : String) : LiveData<List<Note>>{
        return repository.searchNotes(searchQuery).asLiveData()
    }

    //todas las categorias
    val allCategories : LiveData<List<String>> = repository.getAllCategories().asLiveData()

    //filtrar porcategories
    fun getNotesByCategory(category : String): LiveData<List<Note>>{
        return repository.getNotesByCategory(category).asLiveData()
    }

}