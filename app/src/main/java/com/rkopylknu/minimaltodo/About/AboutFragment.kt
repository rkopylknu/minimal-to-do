package com.rkopylknu.minimaltodo.About

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultFragment
import com.rkopylknu.minimaltodo.R

class AboutFragment : AppDefaultFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appVersion = (activity as? AboutActivity)?.appVersion ?: ""

        val versionTextView =
            view.findViewById<TextView>(R.id.aboutVersionTextView)
        versionTextView.text = getString(R.string.app_version, appVersion)
    }

    @LayoutRes
    override fun layoutRes() = R.layout.fragment_about

    companion object {

        fun newInstance() = AboutFragment()
    }
}