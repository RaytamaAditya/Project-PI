package com.example.myappplant.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.myappplant.database.AnalysisResult

@Dao
interface AnalysisResultDao {
    @Query("SELECT * FROM analysis_results")
    fun getAllAnalysisResults(): LiveData<List<AnalysisResult>>

    @Query("DELETE FROM analysis_results")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(analysisResult: AnalysisResult)

    @Insert
    suspend fun insert(analysisResult: AnalysisResult)

    @Query("SELECT * FROM analysis_results WHERE imageUri = :imageUri LIMIT 1")
    suspend fun getAnalysisResultByImageUri(imageUri: String): AnalysisResult?
}
