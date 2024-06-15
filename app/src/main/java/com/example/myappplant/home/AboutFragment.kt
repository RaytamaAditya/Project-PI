package com.example.myappplant.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.myappplant.R
import com.example.myappplant.home.InfoDialogFragment

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val buttonShowPopup: ImageButton = view.findViewById(R.id.buttonShowPopup)
        buttonShowPopup.setOnClickListener {
            showPopup()
        }
        return view
    }

    private fun showPopup() {
        val dialog = InfoDialogFragment()
        dialog.show(parentFragmentManager, "InfoDialogFragment")
    }
}