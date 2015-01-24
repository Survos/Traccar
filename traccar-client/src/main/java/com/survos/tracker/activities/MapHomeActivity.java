package com.survos.tracker.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.survos.tracker.AboutActivity;
import com.survos.tracker.Constants.Constants;
import com.survos.tracker.R;
import com.survos.tracker.StatusActivity;
import com.survos.tracker.TraccarActivity;
import com.survos.tracker.TraccarService;
import com.survos.tracker.data.DBInterface;
import com.survos.tracker.data.DatabaseColumns;
import com.survos.tracker.data.Logger;
import com.survos.tracker.data.SQLiteLoader;
import com.survos.tracker.data.TableLocationPoints;
import com.survos.tracker.dialogs.AgreementDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MapHomeActivity extends ActionBarActivity implements DBInterface.AsyncDbQueryCallback,
        LoaderManager.LoaderCallbacks<Cursor>,GoogleMap.OnMapLoadedCallback,DialogInterface.OnClickListener {

    //private GoogleMap mGmap;
    private Switch mSwitch;
    private SharedPreferences mSharedPreferences;

    private TextView mLocationText;

    private static TextView mStateText;
    String msg="Subject ID";

    /**
     * Reference to the Dialog Fragment for selecting the chat options
     */
    private AgreementDialog mAgreementDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_home);


//        if (savedInstanceState == null) {
//            mGmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//        }
//
//        mGmap.setOnMapLoadedCallback(this);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSwitch = (Switch) findViewById(R.id.location_switch);
        mLocationText = (TextView) findViewById(R.id.location_text);
        mStateText = (TextView) findViewById(R.id.state);

        if (mSharedPreferences.getBoolean(TraccarActivity.KEY_STATUS, false)) {
            mSwitch.setChecked(true);
            addMessage(getResources().getString(R.string.connection_active));
        } else {
            mSwitch.setChecked(false);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(MapHomeActivity.this, TraccarService.class));
                    mSharedPreferences.edit().putBoolean(TraccarActivity.KEY_STATUS, true).commit();
                } else {
                    mSharedPreferences.edit().putBoolean(TraccarActivity.KEY_STATUS, false).commit();
                    stopService(new Intent(MapHomeActivity.this, TraccarService.class));
                }
            }
        });

        if(mSharedPreferences.getBoolean("appOpenFirstTime",true)){
            showAgreementDialog();

        }

        loadLocationPoints();



    }

    public static void addMessage(String message) {
        if(Constants.mainActivityIsOpen()) {
            Log.i(TraccarActivity.LOG_TAG, message);
            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            message = format.format(new Date()) + " - " + message;
            mStateText.setText(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.setMainActivityIsOpen(true);

        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            Constants.APP_VERSION = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Constants.OS_VERSION = android.os.Build.VERSION.RELEASE;
        Constants.MODEL = android.os.Build.MODEL;

        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Constants.UUID = tManager.getDeviceId();
        Constants.context=MapHomeActivity.this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.setMainActivityIsOpen(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("divyesh","on option item selected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            final Intent settings = new Intent(this,
                    TraccarActivity.class);
            startActivity(settings);
            return true;
        }

//        else  if (id == R.id.status) {
//
//            startActivity(new Intent(this, StatusActivity.class));
//
//            return true;
//        }
        else  if (id == R.id.about) {


            startActivity(new Intent(this, AboutActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInsertComplete(int taskId, Object cookie, long insertRowId) {

    }

    @Override
    public void onDeleteComplete(int taskId, Object cookie, int deleteCount) {

    }

    @Override
    public void onUpdateComplete(int taskId, Object cookie, int updateCount) {

    }

    @Override
    public void onQueryComplete(int taskId, Object cookie, Cursor cursor) {

        cursor.moveToFirst();

//        Logger.d("CURSOR", cursor.getCount() + "");
//        Marker[] markers = new Marker[cursor.getCount()];
//        for (int i = 0; i < cursor.getCount(); i++) {
//            if (i + 1 == cursor.getCount()) {
//                Log.d("timee", cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)));
//                mLocationText.setText("Last location updated at " +
//                        cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)));
//            }
//            markers[i] = mGmap.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LATITUDE))),
//                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LONGITUDE)))))
//                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)) + " provider : " +
//
//                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER))));
////            markers[i]
////                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))+" provider : "+
////
////                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER)));
//            cursor.moveToNext();


//        }
//
//        if(cursor.getCount()!=0) {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Marker marker : markers) {
//                builder.include(marker.getPosition());
//            }
//            LatLngBounds bounds = builder.build();
//
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//
//            try {
//                mGmap.moveCamera(cu);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    private void loadLocationPoints() {
        getSupportLoaderManager().restartLoader(Constants.LoaderIds.LOAD_LOCATION, null, MapHomeActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == Constants.LoaderIds.LOAD_LOCATION) {

            return new SQLiteLoader(this, false, TableLocationPoints.NAME, null,
                    null, null, null, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == Constants.LoaderIds.LOAD_LOCATION) {

            Logger.d("TAG", "Cursor Loaded with count: %d", cursor.getCount());

            cursor.moveToFirst();

            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            Logger.d("CURSOR", cursor.getCount() + "");
            Marker[] markers = new Marker[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {

                if (i + 1 == cursor.getCount()) {
                    Log.d("divyesh"," Last location updated at");
                    mLocationText.setText("Last location updated at " +
                            getDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))),
                                    "hh:mma")+"\n\nLatitude : "+cursor.getString(cursor.getColumnIndex(DatabaseColumns.LATITUDE))+"\nLongitude : "+
                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.LONGITUDE)));

//                    mGmap.addMarker(new MarkerOptions()
//                            .position(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LATITUDE))),
//                                    Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LONGITUDE)))))
//                            .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)) + " provider : " +
//
//                                    cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER))));
//                }
                }

//            markers[i]
//                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))+" provider : "+
//
//                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER)));
                cursor.moveToNext();

            }

        }
    }
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onMapLoaded() {
        DBInterface.queryAsync(Constants.QueryTokens.QUERY_LOCATION_POINTS, null, null, true, TableLocationPoints.NAME,
                null, null, null, null, null, null, "30", this);
        loadLocationPoints();

    }

    /**
     * Show dialog for chat options
     */
    private void showAgreementDialog() {
        mAgreementDialog = new AgreementDialog();
        mAgreementDialog
                .show(AlertDialog.THEME_HOLO_LIGHT,0,R.string.agreement,R.string.agree,R.string.disagree,
                        0,0,getSupportFragmentManager(),true,"AGREE_DIALOG","Participants will work with the study researchers to track their location during the study period by using a smartphone app that they will download on their iPhone or Android phone. The app will need to be turned on during the entire study period. Study staff may call, text, or email you to remind you to keep the app turned on.");

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if(which == -1){
            mSharedPreferences.edit().putBoolean("appOpenFirstTime", false).commit();
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Log.d("divyesh","here "+tMgr.getLine1Number());
            if(tMgr.getLine1Number()==null || tMgr.getLine1Number().equalsIgnoreCase(""))
                showMobileNumberDialog();
            else
            {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref.edit().putString("mobilenumber", tMgr.getLine1Number()).commit();
            }
            showSubjectIdDialog();
        }
        else {
            finish();
        }
    }

    private void showSubjectIdDialog(){

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MapHomeActivity.this);
        View promptsView = li.inflate(R.layout.layout_subjectid_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Subject ID");
        //alert.setMessage("Message");

        // Set an EditText view to get user input
        /*final EditText input = new EditText(this);
        input.setHint("Subject ID");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);*/
        alert.setView(promptsView);

        final EditText input = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
//        int maxLength = 9;
//        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        input.setHint(msg);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                // Do something with value!
                if(!value.trim().equalsIgnoreCase("")) {
                    if(validateSubjectID(value)){
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        pref.edit().putString("subject_id", value).commit();
                        Constants.SUBJECT_ID = value;
                    }
                    else {
                        msg ="Enter Valid SubjectId";
                        showSubjectIdDialog();
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public boolean validateSubjectID(String num)
    {
        if(num.length()%2==0)
            return true;
        else
            return false;
    }
    private void showMobileNumberDialog(){

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MapHomeActivity.this);
        View promptsView = li.inflate(R.layout.layout_subjectid_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Mobile Number");
        //alert.setMessage("Message");

        // Set an EditText view to get user input
        /*final EditText input = new EditText(this);
        input.setHint("Subject ID");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);*/
        alert.setView(promptsView);

        final EditText input = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        input.setHint("Mobile Number");

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                // Do something with value!
                if(!value.trim().equalsIgnoreCase("")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    pref.edit().putString("mobilenumber", value).commit();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

}
