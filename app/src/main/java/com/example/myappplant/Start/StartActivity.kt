package com.example.myappplant.Start

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myappplant.R
import com.example.myappplant.view.MainActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val titleText: TextView = findViewById(R.id.titleText)
        val text = "PlantScan."

        val spannableString = SpannableString(text)
        spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        titleText.text = spannableString
        titleText.setTextColor(Color.BLACK) // Warna teks utama
        titleText.paint.strokeWidth = 5f
        titleText.paint.style = Paint.Style.FILL
        titleText.paint.color = Color.WHITE // Warna outline

        val startButton: Button = findViewById(R.id.buttonInCardView)
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}