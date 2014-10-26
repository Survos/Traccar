/*
 * Copyright 2012 - 2014 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

import java.util.regex.Pattern;


/**
 * Main user interface
 */
@SuppressWarnings("deprecation")
public class TraccarActivity extends PreferenceActivity implements View.OnClickListener {

    public static final String LOG_TAG = "traccar";

    public static final String KEY_ID = "id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_EXTENDED = "extended";
    public static final String KEY_STATUS = "status";
    public static final String KEY_RESTRICT_TIME = "time_restrict";
    public static final String KEY_RESTRICT_START_TIME = "restrict_start_time";
    public static final String KEY_RESTRICT_STOP_TIME = "restrict_stop_time";

    /**
     * holds the dialog for selecting time range
     */
    private Dialog  mSelectRestrictTimeDialog;

    /**
     * variable for holding sharedpreferences
     */
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSharedPreferences= getPreferenceScreen().getSharedPreferences();

        initPreferences();
        if (mSharedPreferences.getBoolean(KEY_STATUS, false))
            startService(new Intent(this, TraccarService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                preferenceChangeListener);
        super.onPause();
    }

    OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_STATUS)) {
                if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
                    startService(new Intent(TraccarActivity.this, TraccarService.class));
                } else {
                    stopService(new Intent(TraccarActivity.this, TraccarService.class));
                }
            } else if (key.equals(KEY_ID)) {
                findPreference(KEY_ID).setSummary(sharedPreferences.getString(KEY_ID, null));
            } else if (key.equals(KEY_ADDRESS)) {
                findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, null));

            } else if (key.equals(KEY_RESTRICT_TIME)) {
                if (!sharedPreferences.getBoolean(KEY_RESTRICT_TIME, false)) {

                } else {
                    openSelectRestrictTimeDialog();
                }
            }
        }
    };

    private void openSelectRestrictTimeDialog() {

        // custom dialog
        mSelectRestrictTimeDialog = new Dialog(this);
        mSelectRestrictTimeDialog.setContentView(R.layout.layout_restrict_time_dialog);
        mSelectRestrictTimeDialog.setTitle("Restrict Time");

        final TextView timeDifferenceText = (TextView) mSelectRestrictTimeDialog.findViewById(R.id.time_difference);
        final Button setButton = (Button) mSelectRestrictTimeDialog.findViewById(R.id.set_button);


        //setting up range bar
        RangeBar rangebar = (RangeBar) mSelectRestrictTimeDialog.findViewById(R.id.rangebar);
        rangebar.setTickCount(24);
        rangebar.setTickHeight(25);
        rangebar.setBarWeight(6);
        rangebar.setBarColor(229999999);

        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                timeDifferenceText.setText(leftThumbIndex + " hours - " + rightThumbIndex + " hours");
                mSharedPreferences.edit().putInt(KEY_RESTRICT_START_TIME, leftThumbIndex).commit();
                mSharedPreferences.edit().putInt(KEY_RESTRICT_STOP_TIME, rightThumbIndex).commit();

            }
        });

        setButton.setOnClickListener(this);


        mSelectRestrictTimeDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.status) {
            startActivity(new Intent(this, StatusActivity.class));
            return true;
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TraccarService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String id = telephonyManager.getDeviceId();

        String id = getPrimaryEmailAccount();

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (!sharedPreferences.contains(KEY_ID)) {
            sharedPreferences.edit().putString(KEY_ID, id).commit();
        }

        String serverAddress = sharedPreferences.getString(KEY_ADDRESS, getResources().getString(R.string.settings_address_summary));

        findPreference(KEY_ID).setSummary(sharedPreferences.getString(KEY_ID, id));
        findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, serverAddress));


    }

    private String getPrimaryEmailAccount() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        String possibleEmail = "";
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }
        return possibleEmail;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.set_button) {
            mSelectRestrictTimeDialog.dismiss();
        }
    }
}
