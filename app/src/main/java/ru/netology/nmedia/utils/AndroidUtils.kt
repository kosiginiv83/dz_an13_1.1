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
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}


//    fun showKeyboard(view: View) {
//        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
//        view.post_edit_text.requestFocus()
//        view.post_edit_text.showSoftInputOnFocus
//    }

//inputMethodManager.toggleSoftInputFromWindow(binding.postEditText.applicationWindowToken,
//InputMethodManager.SHOW_FORCED, 0)

//        binding.postEditText.requestFocus()
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//        context?.getSystemService(Context.INPUT_METHOD_SERVICE)
//            .soft(editText, InputMethodManager.SHOW_IMPLICIT)
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//        imm.showSoftInput(binding.postEditText, 0)
//        activity?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
//        imm.showSoftInput(binding.postEditText, InputMethodManager.SHOW_FORCED)
//        binding.postEditText.requestFocus()
