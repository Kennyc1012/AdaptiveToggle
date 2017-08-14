package com.kennyc.adaptivetoggle

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by kcampagna on 8/14/17.
 */
class MainActivity : Activity() {
    private val KEY_IS_HIDDEN = "isHidden"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        configureView()
    }

    private fun configureView() {
        if (Settings.System.canWrite(applicationContext)) {
            permissionView.visibility = View.GONE
            contentView.visibility = View.VISIBLE
            hideSwitch.isChecked = isAppIconHidden()
            hideSwitch.setOnCheckedChangeListener({ buttonView, isChecked -> setIconHidden(isChecked) })
        } else {
            contentView.visibility = View.GONE
            permissionView.visibility = View.VISIBLE

            permissionBtn.setOnClickListener {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            }
        }
    }

    private fun isAppIconHidden(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_IS_HIDDEN, false)
    }

    private fun setIconHidden(isHidden: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(KEY_IS_HIDDEN, isHidden).apply()
        val state = if (isHidden) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        packageManager.setComponentEnabledSetting(ComponentName(this, MainActivity::class.java), state, PackageManager.DONT_KILL_APP)
    }
}