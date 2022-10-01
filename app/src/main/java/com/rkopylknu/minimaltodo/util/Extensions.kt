package com.rkopylknu.minimaltodo.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.getColorCompat(@ColorRes id: Int) =
    ResourcesCompat.getColor(resources, id, activity?.theme)

val Fragment.appCompatActivity get() = (activity as? AppCompatActivity)