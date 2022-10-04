package com.rkopylknu.minimaltodo.ui.about

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rkopylknu.minimaltodo.BuildConfig
import com.rkopylknu.minimaltodo.R
import androidx.appcompat.app.AppCompatActivity
import com.rkopylknu.minimaltodo.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)

        binding.tvVersion.text = getString(
            R.string.version,
            BuildConfig.VERSION_NAME
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}