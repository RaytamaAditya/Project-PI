package com.example.myappplant.tips

import android.content.res.TypedArray
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplant.R

class TipsAdapter(
    private val names: Array<String>,
    private val descriptions: Array<String>,
    private val photos: TypedArray,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<TipsAdapter.TipsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_plant, parent, false)
        return TipsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        if (position < names.size && position < descriptions.size && position < photos.length()) {
            val shortDescription = if (descriptions[position].length > 100) {
                descriptions[position].substring(0, 100) + "..."
            } else {
                descriptions[position]
            }
            holder.bind(names[position], shortDescription, photos.getResourceId(position, -1), onItemClick)
        } else {
            // Handle the case where position is out of bounds
            Log.e("TipsAdapter", "Index out of bounds: $position")
        }
    }

    override fun getItemCount(): Int = names.size

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        photos.recycle()
    }

    class TipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        private val photoImageView: ImageView = itemView.findViewById(R.id.imageViewTip)

        fun bind(name: String, description: String, photoResId: Int, onItemClick: (Int) -> Unit) {
            nameTextView.text = name
            descriptionTextView.text = description
            if (photoResId != -1) {
                photoImageView.setImageResource(photoResId)
            } else {
                photoImageView.setImageResource(R.drawable.ic_place_holder)
            }

            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }
}