package com.slam.outcomehealthvideos.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

//------------------------------------------------------+
//        | Small Screen | Normal Screen | Large Screen |
//--------+--------------+---------------+--------------|
// Height |     480      |      800      |    1024      |
// Width  |     320      |      480      |     600      |
//------------------------------------------------------+

/**
 * Created by slam on 09/07/2019.
 */
public class DeviceUtils {
    /**
     * Get the device display metrics.
     *
     * Samsung 6S Edge:
     *     density=4.0, width=1440, height=2560, scaledDensity=4.0, xdpi=522.514, ydpi=537.388
     *
     * Google Nexus 5X:
     *     density=2.625, width=1080, height=1794, scaledDensity=2.625, xdpi=422.03, ydpi=424.069
     *
     * @param context
     * @return
     */
    static public DisplayMetrics getDeviceDisplayMetrics(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

    static public int getDeviceDisplayWidthInPixels(Context context) {
        return getDeviceDisplayMetrics(context).widthPixels;
    }

    static public int getDeviceDisplayHeightInPixels(Context context) {
        return getDeviceDisplayMetrics(context).heightPixels;
    }

    /**
     * Get the device display density which is one of the following values:
     * <li>DENSITY_LOW = 120
     * <li><b>DENSITY_MEDIUM  = 160 (Default)</b>
     * <li>DENSITY_HEIGHT  = 240
     * <li>DENSITY_XHEIGHT = 320
     * <li>DENSITY_TV      = 213
     * <p></br>
     * @param context
     * @return
     */
    static public int getDeviceDensity(Context context) {
        DisplayMetrics displayMetrics = getDeviceDisplayMetrics(context);
        return displayMetrics.densityDpi;
    }

    /**
     * Get the dimension of the screen (Left, Top, Right, Bottom).
     *
     * @param activity
     * @return
     */
    static public Rect findPhysicalScreenDimension(Activity activity) {

        if (activity == null) {
            return null;
        }
        Display display = activity.getWindowManager().getDefaultDisplay();

        return findPhysicalScreenDimentions(display);
    }

    /**
     * Get the dimension of the screen (Left, Top, Right, Bottom).
     *
     * @param context
     * @return
     */
    static public Rect findPhysicalScreenDimension(Context context) {

        if (context == null) {
            return null;
        }
        // If you don't have access to an activity to call getWindowManager on. You can use:
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        return findPhysicalScreenDimentions(display);
    }

    /**
     *
     * @param display
     * @return
     */
    static private Rect findPhysicalScreenDimentions(Display display) {
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getWidth").invoke(display);
                heightPixels = (Integer) Display.class.getMethod("getHeight").invoke(display);
            }
            catch (Exception ignored) {
            }

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point size = new Point();
                Display.class.getMethod("getSize", Point.class).invoke(display, size);
                widthPixels = size.x;
                heightPixels = size.y;
            }
            catch (Exception ignored) {

            }
        }
        return new Rect(0, 0, widthPixels, heightPixels);
    }

    /**
     * <li>Small Screen:  320W x  480H</br>
     * <li>Normal Screen: 480W x  800H</br>
     * <li>Large Screen:  600W x 1024H</br>
     * <p></br>
     * @param context
     * @return
     */
    static public boolean isSmallScreenDevice(Context context) {
        DisplayMetrics displayMetrics = getDeviceDisplayMetrics(context);

        if (displayMetrics.widthPixels < 400 || displayMetrics.heightPixels < 500) {
            return true;
        }
        return false;
    }
}