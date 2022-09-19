package com.rkopylknu.minimaltodo.Utility

import com.rkopylknu.minimaltodo.R
import android.content.Context

object Utils {

    fun getToolbarHeight(context: Context): Int {
        val styledAttributes =
            context.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return toolbarHeight
    }
}