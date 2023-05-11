package org.sfy.ttrip.common.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import org.sfy.ttrip.R
import org.sfy.ttrip.databinding.CustomToastBinding

object CustomToast {

    fun createToast(context: Context, message: String): Toast {
        val inflater = LayoutInflater.from(context)
        val binding: CustomToastBinding =
            DataBindingUtil.inflate(inflater, R.layout.custom_toast, null, false)

        binding.tvToastContent.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 100.px(context))
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }
}

fun Int.px(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

fun Context.showToastMessage(message: String) {
    CustomToast.createToast(this, message).show()
}