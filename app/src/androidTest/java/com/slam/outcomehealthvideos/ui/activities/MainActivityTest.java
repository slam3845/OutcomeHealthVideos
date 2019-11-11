package com.slam.outcomehealthvideos.ui.activities;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.rule.ActivityTestRule;

import com.slam.outcomehealthvideos.Constants;
import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.data.VideoData;
import com.slam.outcomehealthvideos.ui.main.MainFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class MainActivityTest {
    static private final String TAG = ActivityTestRule.class.getSimpleName();

    MainActivity _mainActivity;
    MainFragment _mainFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);

//    @Rule
//    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
//        @Override
//        protected void beforeActivityLaunched() {
//            printActivityObject("beforeActivityLaunched() ");
//            Log.e(TAG, " beforeActivityLaunched() _mainActivity.testValue: " + _mainActivity.testValue);
//            Log.e(TAG, "----------------------------------------------------");
//        }
//    };

    @Before
    public void setUp() {
        Log.i(TAG, ">> setUp()");
    }

    @Before
    public void before() {
        Log.i(TAG, ">> before()");
    }

    @Test
    public void onCreate() {
        Log.i(TAG, ">> onCreate()");
    }

    @After
    public void tearDown() throws Exception {
        Log.i(TAG, ">> tearDown()");
    }

    @Test
    public void ensureUiComppnentsArePresent() throws Exception {
        Log.i(TAG, ">> ensureUiComppnentsArePresent()");

        _mainActivity = rule.getActivity();
        assertThat(_mainActivity, notNullValue());

        // The VideoView player
        VideoView videoView = _mainActivity.findViewById(R.id.videoView);
        assertThat(videoView, notNullValue());

        RecyclerView listView = _mainActivity.findViewById(R.id.listViewVideos);
        assertThat(listView, notNullValue());

        ImageView imageViewThumbnail = _mainActivity.findViewById(R.id.ivVideoThumbnail);
        assertThat(imageViewThumbnail, notNullValue());

        TextView tvTimeDuration = _mainActivity.findViewById(R.id.tvDuration);
        assertThat(tvTimeDuration, notNullValue());

        TextView tvTitle = _mainActivity.findViewById(R.id.tvTitle);
        assertThat(tvTitle, notNullValue());

        TextView tvSubtitle= _mainActivity.findViewById(R.id.tvSubTitle);
        assertThat(tvSubtitle, notNullValue());

        TextView tvDescription = _mainActivity.findViewById(R.id.tvDescription);
        assertThat(tvDescription, notNullValue());

        Log.i(TAG, ">> =====================================================================");
    }

    @Test
    public void getVideos() {
        _mainActivity = rule.getActivity();
        assertThat(_mainActivity, notNullValue());

        _mainFragment = _mainActivity.getFragment();
        assertThat(_mainFragment, notNullValue());

        List<VideoData> listVideoData = _mainFragment.getListOfVideoData();
        assertThat(listVideoData, notNullValue());

        for (int iX = 0; iX < listVideoData.size(); ++ iX) {
            pringVideoDataInLogcat(iX);
        }
        Log.i(TAG, ">> =====================================================================");
    }

    private void pringVideoDataInLogcat(int indexVideoData) {
        assertThat(_mainFragment, notNullValue());

        VideoData videoData = _mainFragment.getVideoData(indexVideoData);
        assertThat(videoData, notNullValue());

        // The video "sources" in the videos.json file could have multiple video URLs.
        // But to make it simple for this demo, we're only interested in the first video.
        //
        VideoData.VideoSourceAndDuration videoSourceAndDuration = videoData.listUrlVideoSourceAndDuration.get(0);
        String duration = videoSourceAndDuration.strDuration;
        String sourceFrom = (videoData.localOrOnlineVideo == Constants.VIDEO_SOURCE_ONLINE) ? "Online" : "Local";

        if (duration.isEmpty()) {
            Log.i(TAG,
                ">> Video #" + indexVideoData + " (" + sourceFrom + "); Title = " + videoData.title +
                    "; URL=\"" + videoSourceAndDuration.urlVideoSource + "\""
            );
        }
        else {
            Log.i(TAG,
                ">> Video #" + indexVideoData + " (" + sourceFrom + "); Title = " + videoData.title +
                    " (Duration: " + duration + "); RUL=\"" + videoSourceAndDuration.urlVideoSource + "\""
            );
        }
    }
}