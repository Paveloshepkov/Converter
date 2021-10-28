package com.example.converter.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isNetworkError", "valuteList")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, valuteList: Any?) {
    view.visibility = if (valuteList != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}

