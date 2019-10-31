package com.and2long.applist

import android.app.Application
import com.blankj.utilcode.util.CrashUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CrashUtils.init()
    }

}