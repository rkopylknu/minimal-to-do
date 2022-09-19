package com.rkopylknu.minimaltodo.Utility

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar

class ScrollingFABBehaviour(context: Context, attributeSet: AttributeSet?) :
    CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {

    private val toolbarHeight: Int

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        return dependency is SnackbarLayout || dependency is Toolbar
        //        return (dependency instanceof Snackbar.SnackbarLayout);
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        if (dependency is SnackbarLayout) {
            val finalVal = parent.height.toFloat() - dependency.getY()
            child.translationY = -finalVal
        }
        if (dependency is Toolbar) {
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = child.height + fabBottomMargin
            val finalVal = dependency.getY() / toolbarHeight.toFloat()
            val distFinal = -distanceToScroll * finalVal
            child.translationY = distFinal
        }
        return true
    }

    init {
        toolbarHeight = Utils.getToolbarHeight(context)
    }
}