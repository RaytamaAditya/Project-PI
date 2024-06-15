package com.example.myappplant.tips

import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplant.R

class TipsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var names: Array<String>
    private lateinit var descriptions: Array<String>
    private lateinit var photos: TypedArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("TipsFragment", "Creating view for TipsFragment")
        val view = inflater.inflate(R.layout.fragment_tips, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        names = resources.getStringArray(R.array.Name)
        descriptions = resources.getStringArray(R.array.description)
        photos = resources.obtainTypedArray(R.array.photo)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TipsAdapter(names, descriptions, photos) { position ->
            navigateToDetail(position)
        }
        return view
    }


    private fun navigateToDetail(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position", position)

        val detailFragment = TipDetailFragment()
        detailFragment.arguments = bundle

        // Mengganti fragment saat ini dengan TipDetailFragment
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, detailFragment)
            ?.addToBackStack(null)
            ?.commit()
    }



}

