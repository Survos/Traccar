
package com.survos.tracker.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Locale;


/**
 * @author Anshul Kamboj Table representing a list of users
 */
public class TableLocationPoints {

    private static final String TAG = "TableLocationPoints";

    public static final String NAME = "LOCATION_POINTS";

    public static void create(final SQLiteDatabase db) {

        final String columnDef = TextUtils
                .join(SQLConstants.COMMA, new String[]{
                        String.format(Locale.US, SQLConstants.DATA_INTEGER_PK, BaseColumns._ID),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.ID, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.LATITUDE, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.LONGITUDE, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.TIME, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.ACCURACY, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.ALTITUDE, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.BATTERY_PERCENTAGE, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.USER_NAME, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.PROVIDER, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.BEARING, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.TYPE, ""),
                        String.format(Locale.US, SQLConstants.DATA_TEXT, DatabaseColumns.SPEED, "")


                });

        Logger.d(TAG, "Column Def: %s", columnDef);
        db.execSQL(String
                .format(Locale.US, SQLConstants.CREATE_TABLE, NAME, columnDef));

    }

    public static void upgrade(final SQLiteDatabase db, final int oldVersion,
                               final int newVersion) {

        //Add any data migration code here. Default is to drop and rebuild the table

        if (oldVersion == 1) {
            
            /* Drop & recreate the table if upgrading from DB version 1(alpha version) */
            db.execSQL(String
                    .format(Locale.US, SQLConstants.DROP_TABLE_IF_EXISTS, NAME));
            create(db);

        }
    }
}
