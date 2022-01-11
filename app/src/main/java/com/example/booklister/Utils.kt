package com.example.booklister

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

class Utils {

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            // find currently focused view so we can grab correct window token from it
            var view = activity.currentFocus

            // if no view currently has focus
            // create new one just so we can grab window token from it
            if (view == null) view = View(activity)

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}