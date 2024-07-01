package com.example.week1.utils

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi

object BlurUtils {
    @RequiresApi(Build.VERSION_CODES.S)
    fun blur(imageView: ImageView) {
        val renderEffect = RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP)
        imageView.setRenderEffect(renderEffect)
    }
}