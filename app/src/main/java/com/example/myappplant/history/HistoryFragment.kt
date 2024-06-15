package com.example.myappplant.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplant.R
import com.example.myappplant.database.AppDatabase

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel: HistoryViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).analysisResultDao()
        HistoryViewModelFactory(dao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter { analysisResult ->
            viewModel.deleteAnalysisResult(analysisResult)
        }
        recyclerView.adapter = historyAdapter

        viewModel.analysisResults.observe(viewLifecycleOwner) { analysisResults ->
            historyAdapter.submitList(analysisResults)
        }

        return view
    }
}
