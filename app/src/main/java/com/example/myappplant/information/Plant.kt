package com.example.myappplant.information

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable
