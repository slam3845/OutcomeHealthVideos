package com.slam.outcomehealthvideos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.ThisApp;

/**
 * Created by slam on 09/11/2019.
 */
public class SharedPreferencesEx {
    static private final String TAG = SharedPreferencesEx.class.getSimpleName();

    // The default preference file used by the application is:
    //  mFile = "/data/data/com.outcomehealthvideos/shared_prefs/com.outcomehealthvideos.preferences.xml"
    //
    static public final String OUTCOME_HEALTH_SHARED_PREFERENCES = "com.outcomehealthvideos";

    static protected SharedPreferencesEx _instance = null;
    private SharedPreferences _sharedPreferences = null;

    private SharedPreferencesEx() {
        _sharedPreferences = ThisApp.getInstance().getApplicationContext().getSharedPreferences(
            OUTCOME_HEALTH_SHARED_PREFERENCES, Context.MODE_PRIVATE
        );
    }

    static public SharedPreferencesEx getInstance() {
        if (_instance == null) {
            _instance = new SharedPreferencesEx();
        }
        return _instance;
    }

    public SharedPreferences getSharedPreferences() {
        return _sharedPreferences;
    }

    public void debugCheckPreferenceData(String message, boolean showLog) {
        if (ThisApp.isDebugBuild()) {
            int versionCode = getAppVersionCode();
            String versionName = getAppVersionName();

            boolean retrieveVideoTimeDuration = this.shouldRetrieveVideoTimeDuration();

            if (showLog) {
                Log.d(TAG, message +
                    "\nversionCode = " + versionCode +
                    "\nversionName = " + versionName +
                    "\nretrieveVideoTimeDuration = " + retrieveVideoTimeDuration
                );
            }
        }
    }


    public void cleanupOldData() {
        Log.d(TAG, "*** Clean up and delete all previous preference settings...");
        this.removeAllPreference();
    }

    public void removeSharedPreference(String keyName) {
        if (_sharedPreferences != null) {
            _sharedPreferences.edit().remove(keyName).commit();
        }
    }

    public void removeAllPreference() {
        if (_sharedPreferences != null) {
            _sharedPreferences.edit().clear().commit();
        }
    }

    public String getPrefString(String prefKey, String defaultValue) {
        if (_sharedPreferences != null) {
            try {
                return _sharedPreferences.getString(prefKey, defaultValue);
            }
            catch (Exception ex) {
                Log.e(TAG, "SharedPreferencesEx.getPrefString() Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return defaultValue;
    }

    public void setPrefString(String prefKey, String value) {
        if (_sharedPreferences != null) {
            SharedPreferences.Editor editor = _sharedPreferences.edit();
            editor.putString(prefKey, value);
            editor.commit();    // or editor.apply();
        }
    }

    public int getPrefInt(String prefKey, int defaultValue) {
        try {
            if (_sharedPreferences != null) {
                return _sharedPreferences.getInt(prefKey, defaultValue);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "SharedPreferencesEx.getPrefInt() Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return defaultValue;
    }

    public void setPrefInt(String prefKey, int value) {
        if (_sharedPreferences != null) {
            SharedPreferences.Editor editor = _sharedPreferences.edit();
            editor.putInt(prefKey, value);
            editor.commit();
        }
    }

    public float getPrefFloat(String prefKey, float defaultValue) {
        try {
            if (_sharedPreferences != null) {
                return _sharedPreferences.getFloat(prefKey, defaultValue);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "SharedPreferencesEx.getPrefFloat() Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return defaultValue;
    }

    public void setPrefFloat(String prefKey, float value) {
        if (_sharedPreferences != null) {
            SharedPreferences.Editor editor = _sharedPreferences.edit();
            editor.putFloat(prefKey, value);
            editor.commit();
        }
    }

    public boolean getPrefBool(String prefKey, boolean defaultValue) {
        try {
            if (_sharedPreferences != null) {
                return _sharedPreferences.getBoolean(prefKey, defaultValue);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "SharedPreferencesEx.getPrefBool() Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return defaultValue;
    }

    public void setPrefBool(String prefKey, boolean value) {
        if (_sharedPreferences != null) {
            SharedPreferences.Editor editor = _sharedPreferences.edit();
            editor.putBoolean(prefKey, value);
            editor.commit();
        }
    }


    /*
     * See app's build.gradle
     *
     * android {
     *     versionCode 1
     *     versionName "1.0"
     *     ...
     * }
     *  See Constants.AppVersion.getBuildVersion() for getting versionCode
     *  from the running application instance.
     */
    public int getAppVersionCode() {
        String keyAppVersionCode = ThisApp.getStringFromResId(R.string.pref_key_app_version_code);
        return this.getPrefInt(keyAppVersionCode, -1);
    }

    public void setAppVersionCode(int value) {
        String keyAppVersionCode = ThisApp.getStringFromResId(R.string.pref_key_app_version_code);
        this.setPrefInt(keyAppVersionCode, value);
    }

    public void setAppVersionCode(String versionCode) {
        this.setAppVersionCode(Integer.valueOf(versionCode));
    }

    public String getAppVersionName() {
        String keyAppVersionName = ThisApp.getStringFromResId(R.string.pref_key_app_version_name);
        return this.getPrefString(keyAppVersionName, "");
    }

    public void setAppVersionName(String value) {
        String keyAppVersionName = ThisApp.getStringFromResId(R.string.pref_key_app_version_name);
        this.setPrefString(keyAppVersionName, value);
    }


    /******************************************************************************************
     * Beginning of Preference Page Settings
     *
     */

    /*
     * Preference Settings: To retrieve video time duration or not
     */
    public boolean shouldRetrieveVideoTimeDuration() {
        String keyRetrieveVideoDuration = ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time);
        boolean retrieveVideoTimeDuration = this.getPrefBool(keyRetrieveVideoDuration, true);
        return retrieveVideoTimeDuration;
    }
    public void setShouldRetrieveVideoTimeDuration(boolean value) {
        String keyRetrieveVideoDuration = ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time);
        this.setPrefBool(keyRetrieveVideoDuration, value);
    }

    public boolean shouldCacheThumbnailImages() {
        String keyCacheThumbnailImages = ThisApp.getStringFromResId(R.string.pref_key_cache_thumbnail_images);
        boolean cacheThumbnailImages = this.getPrefBool(keyCacheThumbnailImages, true);
        return cacheThumbnailImages;
    }
    public void setShouldCacheThumbnailImages(boolean value) {
        String keyCacheThumbnailImages = ThisApp.getStringFromResId(R.string.pref_key_cache_thumbnail_images);
        this.setPrefBool(keyCacheThumbnailImages, value);
    }

    public boolean shouldRetrieveThumbnailFromVideo() {
        String keyRetrieveVideoDuration = ThisApp.getStringFromResId(R.string.pref_key_retrieve_thumbnail_from_video);
        boolean retrieveThumbnailFromVideo = this.getPrefBool(keyRetrieveVideoDuration, true);
        return retrieveThumbnailFromVideo;
    }
    public void setRetrieveThumbnailFromVideo(boolean value) {
        String keyRetrieveThumbnailFromVideo = ThisApp.getStringFromResId(R.string.pref_key_retrieve_thumbnail_from_video);
        this.setPrefBool(keyRetrieveThumbnailFromVideo, value);
    }

    public int getThumbnailFrameAtTime() {
        String keyThumbnailAtTime = ThisApp.getStringFromResId(R.string.pref_key_thumbnail_frame_at_time);
        int thumbnailAtTime = this.getPrefInt(keyThumbnailAtTime, 5000);
        return thumbnailAtTime;
    }
    public void setThumbnailFrameAtTime(int ThumbnailAtTime) {

        String keyThumbnailAtTime = ThisApp.getStringFromResId(R.string.pref_key_thumbnail_frame_at_time);
        this.setPrefInt(keyThumbnailAtTime, ThumbnailAtTime);
    }
    /**** End of Preference Page Settings ********************************************************/
}
