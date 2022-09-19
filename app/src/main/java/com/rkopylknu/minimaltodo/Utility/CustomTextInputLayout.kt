package com.rkopylknu.minimaltodo.Utility

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout(
    context: Context,
    attrs: AttributeSet?
) : TextInputLayout(context, attrs) {

    private var mIsHintSet = false
    private var mHint: CharSequence? = null

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is EditText) {
            // Since hint will be nullify on EditText once on parent addView, store hint value locally
            mHint = child.hint
        }
        super.addView(child, index, params)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mIsHintSet && ViewCompat.isLaidOut(this)) {
            // We have to reset the previous hint so that equals check pass
            hint = null

            // In case that hint is changed programatically
            val currentEditTextHint = editText!!.hint
            if (currentEditTextHint != null && currentEditTextHint.isNotEmpty()) {
                mHint = currentEditTextHint
            }
            hint = mHint
            mIsHintSet = true
        }
    }
}