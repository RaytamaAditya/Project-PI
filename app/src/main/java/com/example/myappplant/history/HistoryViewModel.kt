package com.example.myappplant.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplant.database.AnalysisResult
import com.example.myappplant.database.AnalysisResultDao
import kotlinx.coroutines.launch

class HistoryViewModel(private val dao: AnalysisResultDao) : ViewModel() {

    val analysisResults: LiveData<List<AnalysisResult>> = dao.getAllAnalysisResults()

    fun deleteAnalysisResult(analysisResult: AnalysisResult) {
        viewModelScope.launch {
            dao.delete(analysisResult)
        }
    }
}
