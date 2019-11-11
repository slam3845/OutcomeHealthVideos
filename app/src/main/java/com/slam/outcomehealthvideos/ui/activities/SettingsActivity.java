package com.slam.outcomehealthvideos.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.ThisApp;
import com.slam.outcomehealthvideos.utils.SharedPreferencesEx;

/**
 * Created by slam on 9/11/2019.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    SharedPreferences _activitySharedPreferences;
    MainActivity _mainActivity = null;
    boolean _prevShouldRetrieveThumbnailFromVideo;
    boolean _prevShouldCachedThumbnailImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_settings);

        if (getIntent() != null){
            _mainActivity = (MainActivity) getIntent().getSerializableExtra("MainActivity");

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                _mainActivity = (MainActivity)bundle.get("MainActivity");
            }
        }
        _prevShouldCachedThumbnailImages= SharedPreferencesEx.getInstance().shouldCacheThumbnailImages();
        _prevShouldRetrieveThumbnailFromVideo = ThisApp.shouldRetrieveThumbnailFromVideo;

        // Import Note:
        // activity_settings.xml cannot be inflated using the inflater, because the inflater
        // does not understand with the tag <PreferenceScreen> Instead of setContentView(),
        // call addPreferencesFromResource().  However, addPreferencesFromResource() was
        // deprecated for API Level > 10.
        //
        // For API Level > 10, use PreferenceFragment instead.
        // https://developer.android.com/reference/android/preference/PreferenceActivity.html
        //
        // The non-deprecated approach is to use PreferenceFragment in conjunction with
        // PreferenceActivity, as is described in the PrefereceActivity documentation.
        // If your app is only supporting API Level 11 and higher, just use that.
        //
        // if (android.os.Build.VERSION.SDK_INT >= 11) { /* Honeycomd */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.layout.activity_settings);
        }
        PreferenceScreen prefScreen = getPreferenceScreen();
        _activitySharedPreferences = prefScreen.getSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _activitySharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        _activitySharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (_mainActivity != null) {
            if (_prevShouldCachedThumbnailImages != ThisApp.shouldUseCachedThumbnailImages ||
                _prevShouldRetrieveThumbnailFromVideo != ThisApp.shouldRetrieveThumbnailFromVideo
            ) {
                if (_mainActivity != null) {
                    _mainActivity.notifyDataSetChanged();
                }
            }
        }
        finish();
    }

    /**
     * This listener method will be called when any of the data is modified.
     *
     * @param sharedPreferences
     * @param keyName
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyName) {
        // The keyName is defined in the preference file (see activity_settings.xml)
        //
        if (isKey(keyName, R.string.pref_key_retrieve_video_duration_time)) {
            String keyRetrieveVideoDuration = ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time);
            ThisApp.shouldRetrieveVideoTimeDuration = _activitySharedPreferences.getBoolean(keyRetrieveVideoDuration, true);
            SharedPreferencesEx.getInstance().setShouldRetrieveVideoTimeDuration(ThisApp.shouldRetrieveVideoTimeDuration);
        }
        else if (isKey(keyName, R.string.pref_key_cache_thumbnail_images)) {
            String keyCacheThumbnailImages = ThisApp.getStringFromResId(R.string.pref_key_cache_thumbnail_images);
            ThisApp.shouldUseCachedThumbnailImages = _activitySharedPreferences.getBoolean(keyCacheThumbnailImages, true);
            SharedPreferencesEx.getInstance().setShouldCacheThumbnailImages(ThisApp.shouldUseCachedThumbnailImages);
        }
        else if (isKey(keyName, R.string.pref_key_retrieve_thumbnail_from_video)) {
            String keyRetrieveVideoThumbnail = ThisApp.getStringFromResId(R.string.pref_key_retrieve_thumbnail_from_video);
            ThisApp.shouldRetrieveThumbnailFromVideo = _activitySharedPreferences.getBoolean(keyRetrieveVideoThumbnail, true);
            SharedPreferencesEx.getInstance().setRetrieveThumbnailFromVideo(ThisApp.shouldRetrieveThumbnailFromVideo);

            if (ThisApp.shouldRetrieveThumbnailFromVideo) {
                // If the checkbox to retrieve thumbnail from video is checked,
                // then show the dialog box to allow the user to enter the time
                // where the thumbnail will be retrieved.
                dlgEnterThumbnailFrameAtTime();
            }

            // Test - enable/disalble the preference menu item for showing the thumbnail time dialog.
            //
            String prefKeyNameThumbnailFrameAtTime = ThisApp.getStringFromResId(R.string.pref_key_thumbnail_frame_at_time);
            getPreferenceScreen().findPreference(prefKeyNameThumbnailFrameAtTime).setEnabled(ThisApp.shouldRetrieveThumbnailFromVideo);
        }
        else if (isKey(keyName, R.string.pref_key_thumbnail_frame_at_time)) {
            // Do nothing...
            // Just for testing to show the preference menu item with enable/disable feature.
        }
    }

    /**
     * Show the dialog box to allow the user to enter the time
     * where the thumbnail will be retrieved
     */
    private void dlgEnterThumbnailFrameAtTime() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.dialog_title_thumbnail_frame_at_time);
        alertDialog.setMessage(R.string.dialog_msg_thumbnail_frame_at_time);

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(lp);
        input.setText(String.valueOf(ThisApp.videoThumbnailAtTimeInMilliSeconds));
        input.setSingleLine();
        input.setPadding(
            getResources().getInteger(R.integer.dialog_edit_text_padding_left), 0,
            0,  getResources().getInteger(R.integer.dialog_edit_text_padding_bottom)
        );
        // input.setPadding(getResources().getDimensionPixelSize(R.dimen.dialog_edit_text_margin), 0, 0, 50);

        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_video_reel_small);

        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ThisApp.videoThumbnailAtTimeInMilliSeconds = Integer.valueOf(input.getText().toString());
                        SharedPreferencesEx.getInstance().setThumbnailFrameAtTime(ThisApp.videoThumbnailAtTimeInMilliSeconds );
                    }
                });

        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    /**
     * This is a sanity check to make sure the key is indeed a valid
     * key defined in the preference file (see activity_settings.xml)
     *
     * @param keyName
     * @param prefKeyResId
     * @return
     */
    private boolean isKey(String keyName, int prefKeyResId) {
        String prefKeyName = ThisApp.getStringFromResId(prefKeyResId);
        if (keyName.equals(prefKeyName)) {
            return true;
        }
        return false;
    }

    private void pickPreferenceObject(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory cat = (PreferenceCategory) p;
            for (int i = 0; i < cat.getPreferenceCount(); i++) {
                pickPreferenceObject(cat.getPreference(i));
            }
        } else {
            initSummary(p);
        }
    }

    private void initSummary(Preference p) {

        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
        // More logic for ListPreference, etc...
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
