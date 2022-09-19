package com.rkopylknu.minimaltodo.Utility

import com.rkopylknu.minimaltodo.R
import android.content.res.Resources

/**
 * Created by avjindersinghsekhon on 9/21/15.
 */
class PreferenceKeys(resources: Resources) {

    val night_mode_pref_key: String

    init {
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key)
    }
}