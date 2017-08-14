package com.kennyc.adaptivetoggle

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Created by kcampagna on 8/14/17.
 */
class BrightnessService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        if (Settings.System.canWrite(applicationContext)) {
            try {
                val cr = contentResolver
                val isAdaptive = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                val brightnessMode = if (isAdaptive) Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL else Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode)
                updateTile()
            } catch (ex: Settings.SettingNotFoundException) {
                // TODO?
            }

        } else {
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
    }

    private fun updateTile() {
        val context = applicationContext
        val tile = qsTile

        if (Settings.System.canWrite(context)) {
            try {
                val isAdaptive = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                val icon = if (isAdaptive) R.drawable.ic_brightness_auto_white_24dp else R.drawable.ic_brightness_6_white_24dp
                val label = context.getString(if (isAdaptive) R.string.adaptive_on else R.string.manual_on)
                tile.label = label
                tile.icon = Icon.createWithResource(context, icon)
            } catch (ex: Settings.SettingNotFoundException) {
                // TODO?
            }

        } else {
            tile.label = context.getString(R.string.permission_needed)
            tile.icon = Icon.createWithResource(context, R.drawable.ic_permission_alert_white_24dp)
        }

        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }
}