package com.survos.tracker.Constants;

/**
 * Created by anshul1235 on 09/11/14.
 */
public class Constants {

    private static boolean mMainActivityIsOpen;


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
}
