package com.and2long.applist

import android.graphics.drawable.Drawable

data class AppInfo(
    var appName: String = "",
    var appPackage: String = "",
    var verName: String = "",
    var appIcon: Drawable? = null
)

