package com.jiashie.mmbase64

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter

object Util {
    private fun isAccessibilityEnable(context: Context): Boolean {
        var value = 0
        try {
            value = Settings.Secure.getInt(context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return value != 0
    }

    /**
     *
     * @param context
     * @return
     */
    fun isServiceEnabled(context: Context): Boolean {
        if (isAccessibilityEnable(context)) {
            val str = Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (str != null) {
                val strSplitter = SimpleStringSplitter(':')
                strSplitter.setString(str)
                val target =
                    context.packageName + "/" + MyService::class.java.getName()
                while (strSplitter.hasNext()) {
                    if (strSplitter.next().equals(target, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 跳转到系统设置的辅助功能界面
     * @param context
     * @return
     */
    fun jumpToAccessibilitySettings(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            return true
        } catch (localRuntimeException: RuntimeException) {
            intent.action = Settings.ACTION_SETTINGS
            context.startActivity(intent)
        }
        return false
    }
}