package com.slam.outcomehealthvideos.ui.main;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.slam.outcomehealthvideos.data.VideoData;

/**
 * Created by slam on 09/05/2019.
 */
public class MainViewModel extends ViewModel {
    static private final String TAG = MainViewModel.class.getSimpleName();

    private VideoData _videoData = new VideoData();

    public VideoData getVideoData() {
        return _videoData;
    }

    /**
     * Set the LiveData with the VideoData to play in the VideoView.
     * All the observers will be notified with the new VideoData change.
     * (e.g. See MainFragment.onActivityCreated())
     *
     * @param videoData
     */
    public void setVideoData(VideoData videoData) {
        if (_videoData.hasObservers()) {
            Log.d(TAG, "MainViewModel.setVideoData()");
        }
         _videoData.setValue(videoData);
    }
}
