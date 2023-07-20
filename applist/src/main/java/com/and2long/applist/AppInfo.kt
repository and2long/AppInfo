package com.and2long.applist

import android.graphics.drawable.Drawable

data class AppInfo(
    var appName: String = "",
    var packageName: String = "",
    var versionName: String = "",
    var versionCode: String = "",
    var appIcon: Drawable? = null
)

