package com.survos.tracker;

import android.app.Application;
import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;


/**
 * Custom Application class which holds some common functionality for the
 * Application
 *
 * @author Anshul Kamboj
 */
public class TrackerApplication extends Application {

    private static final String TAG = "YeloApplication";


    /**
     * Maintains a reference to the application context so that it can be
     * referred anywhere wihout fear of leaking. It's a hack, but it works.
     */
    private static Context sStaticContext;


    /**
     * Gets a reference to the application context
     */
    public static Context getStaticContext() {
        if (sStaticContext != null) {
            return sStaticContext;
        }

        //Should NEVER hapen
        throw new RuntimeException("No static context instance");
    }

    @Override
    public void onCreate() {

        sStaticContext = getApplicationContext();


    }


    /**
     * Some device manufacturers are stuck in the past and stubbornly use H/W
     * menu buttons, which is deprecated since Android 3.0. This breaks the UX
     * on newer devices since the Action Bar overflow just doesn't show. This
     * little hack tricks the Android OS into thinking that the device doesn't
     * have a permanant menu button, and hence the Overflow button gets shown.
     * This doesn't disable the Menu button, however. It will continue to
     * function as normal, so the users who are already used to it will be able
     * to use it as before
     */
    private void overrideHardwareMenuButton() {
        try {
            final ViewConfiguration config = ViewConfiguration.get(this);
            final Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (final Exception ex) {
            // Ignore since we can't do anything
        }

    }



}
