package com.palacios.daily_notes.data.noteDataBase


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.data.dao.NoteDao

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDataBase : RoomDatabase(){
    abstract fun noteDao() : NoteDao
    companion object{
        @Volatile
        private var INSTANCE : NoteDataBase? = null

        fun getDatabase(context : Context): NoteDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDataBase::class.java,
                    "Note_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}