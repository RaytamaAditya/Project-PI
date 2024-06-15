package com.example.myappplant.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myappplant.R
import com.example.myappplant.about.AboutFragment
import com.example.myappplant.history.HistoryFragment
import com.example.myappplant.home.HomeFragment
import com.example.myappplant.tips.TipsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // Warna saat item terpilih
                intArrayOf(-android.R.attr.state_checked)  // Warna saat item tidak terpilih
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.warna),  // Warna terpilih
                ContextCompat.getColor(this, R.color.black)  // Warna tidak terpilih
            )
        )
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_tips -> {
                    replaceFragment(TipsFragment())
                    true
                }
                R.id.nav_about -> {
                    replaceFragment(AboutFragment())
                    true
                }
                R.id.nav_history -> {  // Tambahkan ini untuk HistoryFragment
                    replaceFragment(HistoryFragment())
                    true
                }
                else -> false
            }
        }
        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}