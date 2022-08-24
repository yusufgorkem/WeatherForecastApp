package com.theappland.weatherforecastapp.utils

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

fun placeHolderProgressBar(context : Context) : CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}