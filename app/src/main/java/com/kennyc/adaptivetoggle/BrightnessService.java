package com.kennyc.adaptivetoggle;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/**
 * Created by kcampagna on 1/5/17.
 */
public class BrightnessService extends TileService {

    @Override
    public void onStartListening() {
        updateTile();
    }

    @Override
    public void onClick() {
        if (Settings.System.canWrite(getApplicationContext())) {
            try {
                ContentResolver cr = getContentResolver();
                boolean isAdaptive = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, isAdaptive ? Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL : Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                updateTile();
            } catch (Settings.SettingNotFoundException ex) {
                // TODO?
            }
        } else {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void updateTile() {
        Context context = getApplicationContext();
        Tile tile = getQsTile();

        if (Settings.System.canWrite(context)) {
            try {
                boolean isAdaptive = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
                int icon = isAdaptive ? R.drawable.ic_brightness_auto_white_24dp : R.drawable.ic_brightness_6_white_24dp;
                String label = context.getString(isAdaptive ? R.string.adaptive_on : R.string.manual_on);
                tile.setLabel(label);
                tile.setIcon(Icon.createWithResource(context, icon));
            } catch (Settings.SettingNotFoundException ex) {
                // TODO?
            }
        } else {
            tile.setLabel(context.getString(R.string.permission_needed));
            tile.setIcon(Icon.createWithResource(context, R.drawable.ic_permission_alert_white_24dp));
        }

        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
    }
}
