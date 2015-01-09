package com.survos.tracker.Constants;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by anshul1235 on 09/11/14.
 */
public class Constants {

    private static boolean mMainActivityIsOpen;
    public static String AGREEMENT_URL = "http://logisticinfotech.net/client/TeliTax/agreement.html";

    public interface QueryTokens {
        //QUERY TOKENS
        public static final int QUERY_LOCATION_POINTS = 100;


        //UPDATE TOKENS

        //INSERT TOKENS

        //
    }

    public interface LoaderIds{
        public static final int LOAD_LOCATION = 1;
    }

    public static boolean mainActivityIsOpen() {
        return mMainActivityIsOpen;
    }

    public static void setMainActivityIsOpen(boolean mainActivityIsOpen) {
        mMainActivityIsOpen = mainActivityIsOpen;
    }

    public static String APP_VERSION;
    public static String OS_VERSION;
    public static String MODEL;
    public static String SUBJECT_ID;
    public static String UUID;
    public static Context context;
    public static String WEB_API="https://posse.survos.com/api/queue/positions?";

}
