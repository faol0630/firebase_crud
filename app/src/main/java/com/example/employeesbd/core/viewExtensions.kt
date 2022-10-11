package com.example.employeesbd.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hide(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hideKeyboard(){
    val inputMethodManager1 = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager1.hideSoftInputFromWindow(windowToken, 0 )
}