package com.rkopylknu.minimaltodo.ui.about

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rkopylknu.minimaltodo.BuildConfig
import com.rkopylknu.minimaltodo.R

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvVersion = view.findViewById<TextView>(R.id.tv_version)
        tvVersion.text = getString(
            R.string.version,
            BuildConfig.VERSION_NAME
        )
    }
}