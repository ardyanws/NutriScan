package com.ardyan.capstonenutriscan.utils

import android.content.Context
import java.io.File

object Utils {
    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile("image", ".jpg", filesDir)
    }
}
