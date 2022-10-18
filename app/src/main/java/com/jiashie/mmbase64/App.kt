package com.jiashie.mmbase64

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        const val DEF_PWD = "bigBrotherIsWatchingYou"
        var context: Context by Delegates.notNull()
            private set

        var spOpenFlag by Preference("open", false)
        var spPwd: String by Preference("pwd", DEF_PWD)
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}