package com.example.myappplant.tips

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myappplant.R
import com.example.myappplant.databinding.FragmentTipDetailBinding

class TipDetailFragment : Fragment() {
    private var _binding: FragmentTipDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTipDetailBinding.inflate(inflater, container, false)


        val position = arguments?.getInt("position") ?: 0
        val names = resources.getStringArray(R.array.Name)
        val photos = resources.obtainTypedArray(R.array.photo)
        val photoResId = photos.getResourceId(position, -1)

        binding.imageViewDetail.setImageResource(photoResId)
        binding.textViewTitleDetail.text = names[position]

        // Set the description based on the position
        val description = when (position) {
            0 -> getString(R.string.tomato_healthy_description)
            1 -> getString(R.string.tomato_septoria_leaf_spot_description)
            2 -> getString(R.string.tomato_bacterial_spot_description)
            3 -> getString(R.string.tomato_blight_description)
            4 -> getString(R.string.tomato_spider_mite_description)
            5 -> getString(R.string.tomato_leaf_mold_description)
            6 -> getString(R.string.tomato_yellow_leaf_curl_virus_prevention)
            7 -> getString(R.string.cabbage_healthy_description)
            8 -> getString(R.string.cabbage_black_rot_description)
            9 -> getString(R.string.soy_frogeye_leaf_spot_description)
            10 -> getString(R.string.Soy_Downy_Mildew_description)
            11 -> getString(R.string.Soy_Healthy_description)
            12 -> getString(R.string.Maize_Ravi_Corn_Rust_description)
            13 -> getString(R.string.Maize_Healthy_description)
            14 -> getString(R.string.Maize_Grey_Leaf_Spot_description)
            15 -> getString(R.string.Maize_Lethal_Necrosis_description)
            else -> getString(R.string.default_description)
        }

        binding.textViewDescriptionDetail.text = description

        photos.recycle()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}