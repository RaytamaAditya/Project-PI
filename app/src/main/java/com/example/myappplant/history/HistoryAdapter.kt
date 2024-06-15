package com.example.myappplant.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myappplant.R
import com.example.myappplant.database.AnalysisResult

class HistoryAdapter(private val onDeleteClick: (AnalysisResult) -> Unit) :
    ListAdapter<AnalysisResult, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteClick)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val labelTextView: TextView = itemView.findViewById(R.id.labelTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        private val deleteButton: ImageView = itemView.findViewById(R.id.my_button)


        fun bind(analysisResult: AnalysisResult, onDeleteClick: (AnalysisResult) -> Unit) {
            Glide.with(itemView.context)
                .load(analysisResult.imageUri)
                .into(imageView)
            labelTextView.text = analysisResult.label
            scoreTextView.text = String.format("%.0f%%", analysisResult.score * 100)

            deleteButton.setOnClickListener {
                onDeleteClick(analysisResult)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AnalysisResult>() {
        override fun areItemsTheSame(oldItem: AnalysisResult, newItem: AnalysisResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AnalysisResult, newItem: AnalysisResult): Boolean {
            return oldItem == newItem
        }
    }
}
