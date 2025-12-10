package com.example.errenteriaapp.components

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.errenteriaapp.R

fun showFeedbackToast(
    context: Context,
    message: String,
    isSuccess: Boolean
) {
    val inflater = LayoutInflater.from(context)
    val toastLayout = inflater.inflate(R.layout.toast_feedback, null)
    val icon = toastLayout.findViewById<ImageView>(R.id.toast_icon)
    val text = toastLayout.findViewById<TextView>(R.id.toast_message)
    text.text = message
    icon.setImageResource(if (isSuccess) R.drawable.ic_toast_success else R.drawable.ic_toast_error)

    Toast(context).apply {
        duration = Toast.LENGTH_SHORT
        view = toastLayout
        show()
    }
}

