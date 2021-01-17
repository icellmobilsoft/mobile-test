package com.mstoica.dogoapp.commons.utils

import android.content.Context
import com.mstoica.dogoapp.R

object ScreenHelper {
    fun isTablet(context: Context) = context.resources.getBoolean(R.bool.isTablet)
}