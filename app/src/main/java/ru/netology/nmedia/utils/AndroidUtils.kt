package ru.netology.nmedia.utils

import android.app.Activity
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethod.SHOW_EXPLICIT
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_new_post.view.*


object AndroidUtils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
