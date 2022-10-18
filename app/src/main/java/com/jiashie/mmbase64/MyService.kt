package com.jiashie.mmbase64

import android.accessibilityservice.AccessibilityService
import android.app.ProgressDialog.show
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast


class MyService : AccessibilityService() {
    companion object {
        /**
         * (输入框里)标记需要加密的内容开始前缀
         */
        private const val ENCODE_START = "wm:"

        /**
         * (消息列表里)标记需要解密的内容开始前缀
         */
        private const val DECODE_START = "mw:"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val source = it.source ?: return

            if (TextUtils.isEmpty(source.text)) {
                return
            }
            if (!App.spOpenFlag) {
                return
            }
            if (TextUtils.isEmpty(App.spPwd)) {
                return
            }

            if ("android.widget.EditText" == source.className) {
                handleEditTextInputEvent(it)
            } else if ("android.widget.TextView" == source.className) {
                handleMsgTextViewEvent(it)
            }
        }
    }

    override fun onInterrupt() {
    }

    /**
     * 处理在输入框长按选中的事件
     *
     * @param event
     */
    private fun handleEditTextInputEvent(event: AccessibilityEvent) {
        val text = event.source.text
        if (text.length <= ENCODE_START.length) {
            return
        }
        if (text.startsWith(ENCODE_START)) {
            val raw = text.substring(ENCODE_START.length)
            val encode = ThreeDESUtil.encrypt(App.spPwd, raw)
            if (TextUtils.isEmpty(encode)) {
                return
            }
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                DECODE_START + encode)
            event.source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        } else if (text.startsWith(DECODE_START)) {
            //解码
            val encoded = text.substring(DECODE_START.length)
            val decode = ThreeDESUtil.decrypt(App.spPwd, encoded)
            if (TextUtils.isEmpty(decode)) {
                return
            }
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                ENCODE_START + decode)
            event.source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }

    }

    /**
     * 处理在消息列表长按选中的事件
     *
     * @param event
     */
    private fun handleMsgTextViewEvent(event: AccessibilityEvent) {
        val text = event.source.text
        if (text.length <= DECODE_START.length || !text.startsWith(DECODE_START)) {
            return
        }
        val encoded = text.substring(DECODE_START.length)
        val decode = ThreeDESUtil.decrypt(App.spPwd, encoded)
        if (TextUtils.isEmpty(decode)) {
            return
        }
        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            decode)
        event.source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

        val bounds = Rect()
        event.source.getBoundsInScreen(bounds)

        toast(decode, bounds.centerY())

    }

    private fun toast(msg: String, yOffset: Int) {
        //targetSdk不能>=30,否则在后台时不能显示自定义view的toast; 且默认toast不能修改位置
        Toast.makeText(this, msg, Toast.LENGTH_LONG).also {
            it.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        }.show()

        /*Toast(this).apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
            val view = TextView(this@MyService)
            view.text = msg
            setView(view)
            duration = Toast.LENGTH_LONG
            show()
        }*/
    }
}