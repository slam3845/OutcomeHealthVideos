package com.slam.outcomehealthvideos;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.slam.outcomehealthvideos.utils.SharedPreferencesEx;

/**
 * This is a singleton object which will be called prior to any UI activity
 * is launch (see <application> in AndroidManifest.xml)
 *
 * Created by slam on 09/05/2019.
 */
public class ThisApp extends Application {
    static private final String TAG = ThisApp.class.getSimpleName();

    static protected ThisApp _instance = null;
    private int _versionCode = -1;
    private String _versionName = "1.0.0";

    /**
     * Preference Settings:
     */
    static public boolean shouldRetrieveVideoTimeDuration = true;
    static public boolean shouldUseCachedThumbnailImages = true;
    static public boolean shouldRetrieveThumbnailFromVideo = true;
    static public int videoThumbnailAtTimeInMilliSeconds = 1000;    // milliseconds.

    static public Boolean _isDebugBuild = null;

    /**
     *  Must include ThisApp in AndroidManifest.xml, such that this application
     *  object "ThisApp" will be automatically initialized at app launch.
     *
     *  <application android:name=".ThisApp" />
     */
    public ThisApp() {
        _instance = this;
    }

    static public ThisApp getInstance() {
        return _instance;
    }

    static public Context getContext() {
        return _instance.getApplicationContext();
    }

    static public int getVersionCode() {
        return _instance._versionCode;
    }

    static public String getVersionName() {
        return _instance._versionName;
    }

    static public String getStringFromResId(int resId) {
        return getContext().getResources().getString(resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Should the video time duration be retrieved and shown on the UI?
        // This could be changed in the SettingsActivity.
        // Note that: If it is true, the app will start up a little slower, because
        // the gallery list will have to fetch the time duration from the video clips.
        //
        shouldRetrieveVideoTimeDuration = SharedPreferencesEx.getInstance().shouldRetrieveVideoTimeDuration();
        shouldUseCachedThumbnailImages = SharedPreferencesEx.getInstance().shouldCacheThumbnailImages();
        shouldRetrieveThumbnailFromVideo = SharedPreferencesEx.getInstance().shouldRetrieveThumbnailFromVideo();
        videoThumbnailAtTimeInMilliSeconds = SharedPreferencesEx.getInstance().getThumbnailFrameAtTime();
    }

    /**
     * Check to see if it is a debug build.
     * In some cases, we can implement additional test feature to run ONLY in the DEBUG build.
     *
     * @return
     */
    static public boolean isDebugBuild() {

        Context context = ThisApp.getInstance().getApplicationContext();
        boolean isDebuggable =  ( 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) );

        if (ThisApp.getInstance()._isDebugBuild == null) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

                int resultLogicalAND = (pi.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE);
                ThisApp.getInstance()._isDebugBuild = (resultLogicalAND != 0);
                Log.d(TAG,
                        "*** [ApplicationInfo.FLAG_DEBUGGABLE = " +
                                Integer.toBinaryString(ApplicationInfo.FLAG_DEBUGGABLE) +
                                "] & [pi.applicationInfo.flags (" + pi.applicationInfo.flags + ") = " +
                                Integer.toBinaryString(pi.applicationInfo.flags) + "] = " +
                                Integer.toBinaryString(resultLogicalAND) + " ***"
                );
            }
            catch(PackageManager.NameNotFoundException nnfe) {
                nnfe.printStackTrace();
                ThisApp.getInstance()._isDebugBuild = false;
            }
        }
        Log.d(TAG, "*** Build: " + (ThisApp.getInstance()._isDebugBuild ? "DEBUG" : "RELEASE") + " ***");
        return ThisApp.getInstance()._isDebugBuild;
    }
}
