package com.kennyc.adaptivetoggle;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends Activity {
    private static final String KEY_IS_HIDDEN = "isHidden";

    private View permissionContainer;

    private View contentView;

    private Switch hideSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionContainer = findViewById(R.id.permissionView);
        contentView = findViewById(R.id.contentView);
        hideSwitch = (Switch) findViewById(R.id.hideSwitch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureView();
    }

    private void configureView() {
        if (Settings.System.canWrite(getApplicationContext())) {
            permissionContainer.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            hideSwitch.setChecked(isAppIconHidden());
            hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setIconHidden(isChecked);
                }
            });
        } else {
            contentView.setVisibility(View.GONE);
            permissionContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.permissionBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            });
        }
    }

    private boolean isAppIconHidden() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_IS_HIDDEN, false);
    }

    private void setIconHidden(boolean isHidden) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(KEY_IS_HIDDEN, isHidden).apply();
        int state = isHidden ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, MainActivity.class), state, PackageManager.DONT_KILL_APP);
    }
}
