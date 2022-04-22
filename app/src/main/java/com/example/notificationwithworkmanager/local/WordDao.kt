package com.example.notificationwithworkmanager.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE )
   suspend fun insertWord(word: Word)

    @Query("DELETE FROM word_table")
    suspend fun deleteAllWords()

    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAllWords(): Flow<List<Word>>

}