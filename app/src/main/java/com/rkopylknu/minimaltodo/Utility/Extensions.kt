package com.rkopylknu.minimaltodo.util

import androidx.annotation.ColorRes
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

fun Snackbar.setOnIgnoredCallback(
    action: (Snackbar?, Int) -> Unit,
): Snackbar {
    object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event == DISMISS_EVENT_TIMEOUT) {
                action(transientBottomBar, event)
            }
        }
    }.also { addCallback(it) }

    return this
}

