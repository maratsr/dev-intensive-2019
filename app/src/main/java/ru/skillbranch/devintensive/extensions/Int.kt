package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
fun Int.dpToPx() =  (this * Resources.getSystem().displayMetrics.density + .5f).toInt()
fun Int.pxToDp() =  (this / Resources.getSystem().displayMetrics.density + .5f).toInt()

