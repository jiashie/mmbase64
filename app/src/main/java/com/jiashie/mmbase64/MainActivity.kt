package com.jiashie.mmbase64

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jiashie.mmbase64.App.Companion.spOpenFlag
import com.jiashie.mmbase64.App.Companion.spPwd
import com.jiashie.mmbase64.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var pendingCheckService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Util.isServiceEnabled(this)) {
            binding.checkbox.isChecked = spOpenFlag
        } else {
            binding.checkbox.isChecked = false
            if (spOpenFlag) {
                // 如果标志是打开，但服务被关闭，说明被杀
                toast("服务被意外中止", 300)
            }
        }
        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (Util.isServiceEnabled(this@MainActivity)) {
                    //本来就已打开，只更改开关标记
                    spOpenFlag = true
                } else {
                    Util.jumpToAccessibilitySettings(this@MainActivity)
                    //onResume回来时再检查isServiceEnabled
                    pendingCheckService = true
                }
            } else {
                spOpenFlag = false

            }
        }

        binding.btnSave.setOnClickListener {
            val pwd = binding.edtPwd.text
            if (TextUtils.isEmpty(pwd)) {
                return@setOnClickListener
            }
            spPwd = pwd.toString()
        }

        binding.btnSave.setOnLongClickListener {
            spPwd = App.DEF_PWD
            binding.edtPwd.setText(App.DEF_PWD)
            true
        }

        binding.edtPwd.setText(spPwd)

        binding.icon.setOnClickListener {
            Util.jumpToAccessibilitySettings(this@MainActivity)
        }

    }

    override fun onResume() {
        super.onResume()
        if (pendingCheckService) {
            val enabled = Util.isServiceEnabled(this)
            if (enabled) {
                spOpenFlag = enabled
            }
            binding.checkbox.isChecked = enabled
            pendingCheckService = false
        }
    }

    private fun toast(msg: String, yOffset: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).also {
            it.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        }.show()
        /*Toast(this).apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
            val view = TextView(this@MainActivity)
            view.setBackgroundColor(Color.GREEN)
            view.text = msg
            setView(view)
            duration = Toast.LENGTH_LONG
            show()
        }*/
    }
}