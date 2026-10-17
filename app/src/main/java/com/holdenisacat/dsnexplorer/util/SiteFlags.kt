package com.holdenisacat.dsnexplorer.util

import androidx.annotation.DrawableRes
import com.holdenisacat.dsnexplorer.R

/** Maps DSN complex names to their flag drawables. */
object SiteFlags {

    @DrawableRes
    fun flagFor(siteName: String?): Int = when (siteName?.lowercase()) {
        "mdscc" -> R.drawable.flag_mdscc
        "gdscc" -> R.drawable.flag_gdscc
        "cdscc" -> R.drawable.flag_cdscc
        else -> R.drawable.flag_unknown
    }
}
