
package com.survos.tracker.data;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility methods for survous
 */
public class Utils {

    private static final String TAG = "Utils";
    public static String MESSAGE_FORMAT = "# %s\n %s  \n\nGet in touch on yelo app: %s";
    public static String SUBJECT = "check this out ";


    /**
     * Checks if the current thread is the main thread or not
     *
     * @return <code>true</code> if the current thread is the main/UI thread, <code>false</code>
     * otherwise
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * Makes an SHA1 Hash of the given string
     *
     * @param string The string to shash
     * @return The hashed string
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String sha1(final String string)
            throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        final byte[] data = digest.digest(string.getBytes());
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }



    public static boolean copyFile(final File src, final File dst) {
        boolean returnValue = true;

        FileChannel inChannel = null, outChannel = null;

        try {

            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();

        } catch (final FileNotFoundException fnfe) {

            Logger.d(TAG, "inChannel/outChannel FileNotFoundException");
            fnfe.printStackTrace();
            return false;
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (final IllegalArgumentException iae) {

            Logger.d(TAG, "TransferTo IllegalArgumentException");
            iae.printStackTrace();
            returnValue = false;

        } catch (final NonReadableChannelException nrce) {

            Logger.d(TAG, "TransferTo NonReadableChannelException");
            nrce.printStackTrace();
            returnValue = false;

        } catch (final NonWritableChannelException nwce) {

            Logger.d(TAG, "TransferTo NonWritableChannelException");
            nwce.printStackTrace();
            returnValue = false;

        } catch (final ClosedByInterruptException cie) {

            Logger.d(TAG, "TransferTo ClosedByInterruptException");
            cie.printStackTrace();
            returnValue = false;

        } catch (final AsynchronousCloseException ace) {

            Logger.d(TAG, "TransferTo AsynchronousCloseException");
            ace.printStackTrace();
            returnValue = false;

        } catch (final ClosedChannelException cce) {

            Logger.d(TAG, "TransferTo ClosedChannelException");
            cce.printStackTrace();
            returnValue = false;

        } catch (final IOException ioe) {

            Logger.d(TAG, "TransferTo IOException");
            ioe.printStackTrace();
            returnValue = false;

        } finally {

            if (inChannel != null) {
                try {

                    inChannel.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return returnValue;
    }

    /**
     * Generate a user's name from the first name last name
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public static String makeUserFullName(String firstName, String lastName) {

        if (TextUtils.isEmpty(firstName)) {
            return "";
        }

        final StringBuilder builder = new StringBuilder(firstName);

        if (!TextUtils.isEmpty(lastName)) {
            builder.append(" ").append(lastName);
        }
        return builder.toString();
    }




    /**
     * Gets the current epoch time. Is dependent on the device's H/W time.
     */
    public static long getCurrentEpochTime() {

        return System.currentTimeMillis() / 1000;
    }


    /**
     * Converts a cursor to a bundle. Field datatypes will be maintained. Floats will be stored in
     * the Bundle as Doubles, and Integers will be stored as Longs due to Cursor limitationcs
     *
     * @param cursor The cursor to convert to a Bundle. This must be positioned to the row to be
     *               read
     * @return The converted bundle
     */
    public static Bundle cursorToBundle(Cursor cursor) {

        final int columnCount = cursor.getColumnCount();
        final Bundle bundle = new Bundle(columnCount);

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {

            final String columnName = cursor.getColumnName(columnIndex);
            switch (cursor.getType(columnIndex)) {

                case Cursor.FIELD_TYPE_STRING: {
                    bundle.putString(columnName, cursor.getString(columnIndex));
                    break;
                }

                case Cursor.FIELD_TYPE_BLOB: {
                    bundle.putByteArray(columnName, cursor.getBlob(columnIndex));
                    break;
                }

                case Cursor.FIELD_TYPE_FLOAT: {
                    bundle.putDouble(columnName, cursor.getDouble(columnIndex));
                    break;
                }

                case Cursor.FIELD_TYPE_INTEGER: {
                    bundle.putLong(columnName, cursor.getLong(columnIndex));
                    break;
                }
            }
        }

        return bundle;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
//            Utils.showMessage(context, "Connection", "Please check internet connection");
        }
        return false;
    }

    public static boolean validateSubjectID(int num)
    {
        if(num%2==0)
            return false;
        else
            return true;
    }
}
