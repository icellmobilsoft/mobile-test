package com.mstoica.dogoapp.commons

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

fun hideInputKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun Activity.handleClearFocusOnTouchOutside(touchInterceptor: View) {
    touchInterceptor.isFocusable = true
    touchInterceptor.isFocusableInTouchMode = true
    touchInterceptor.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let {
                val outRect = Rect()
                it.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.roundToInt(), event.rawY.roundToInt())) {
                    touchInterceptor.requestFocus()
                    hideInputKeyboard(this)
                }
            }
        }
        false
    }
}

fun Fragment.handleClearFocusOnTouchOutside(touchInterceptor: View) =
    requireActivity().handleClearFocusOnTouchOutside(touchInterceptor)