package com.example.notificationwithworkmanager.local

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class WordRepository(private val wordDao: WordDao) {

    var getWords: Flow<List<Word>> = wordDao.getAllWords()


    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insertWord(word)
    }

}