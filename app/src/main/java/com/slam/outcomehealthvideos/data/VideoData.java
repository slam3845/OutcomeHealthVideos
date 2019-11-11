package com.slam.outcomehealthvideos.data;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.slam.outcomehealthvideos.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slam on 09/05/2019.
 */
public class VideoData extends MutableLiveData<VideoData> {

    public class VideoSourceAndDuration {
        // Example of setting the videoSourceAndDuration.urlVideoSource:
        //  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        //
        //  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
        //
        public String urlVideoSource;
        public long lDuration;
        public String strDuration;

        public VideoSourceAndDuration() {
            urlVideoSource = null;
            lDuration = -1;
            strDuration = "";
        }
    }

    public int indexVideo; // 1, 2, 3, ...
    public int localOrOnlineVideo;
    public String description;
    public String title;
    public String subTitle;
    public String urlThumbNail;
    public Bitmap bmpThumbNailCache;   // The user can set this option in SettingsActivity.
    public List<VideoSourceAndDuration> listUrlVideoSourceAndDuration;

    public VideoData() {
        this.indexVideo = -1;
        this.localOrOnlineVideo = Constants.VIDEO_SOURCE_UNKNOWN;
        this.description = "N/A";
        this.title = "Unknown";
        this.subTitle = "";
        this.urlThumbNail = "";
        this.bmpThumbNailCache = null;
        this.listUrlVideoSourceAndDuration = null;
    }

    public VideoData(
        int indexVideo, int videoType, String description, String title, String subTitle,
        String urlThumbNail
    ) {
        this.indexVideo = indexVideo;
        this.localOrOnlineVideo = videoType;
        this.description = description;
        this.title = title;
        this.subTitle = subTitle;
        this.urlThumbNail = urlThumbNail;
        this.bmpThumbNailCache = null;
    }

    /**
     * Example of "urlVideoSource"
     *  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
     *  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
     *
     * @param urlVideoSource
     * @return
     */
    public List<VideoSourceAndDuration> addVideoSourceUrl(String urlVideoSource) {
        if (this.listUrlVideoSourceAndDuration == null) {
            this.listUrlVideoSourceAndDuration = new ArrayList<VideoSourceAndDuration>();
        }
        VideoSourceAndDuration videoSourceAndDuration = new VideoSourceAndDuration();
        videoSourceAndDuration.urlVideoSource = urlVideoSource;

        this.listUrlVideoSourceAndDuration.add(videoSourceAndDuration);
        return this.listUrlVideoSourceAndDuration;
    }

    public int getIndexVideo() {
        return indexVideo;
    }

    public void setIndexVideo(int indexVideo) {
        this.indexVideo = indexVideo;
    }

    /**
     *
     * @param localOrOnlineVideo
     *          Constants.VIDEO_SOURCE_UNKNOWN = 0;
     *          Constants.VIDEO_SOURCE_LOCAL = 1;
     *          Constants.VIDEO_SOURCE_ONLINE = 2;
     */
    public void setVideoSource(int localOrOnlineVideo) {
        this.localOrOnlineVideo = localOrOnlineVideo;
    }

    public boolean isOnlineVideoSource() {
        return this.localOrOnlineVideo == Constants.VIDEO_SOURCE_ONLINE;
    }

    public boolean isLocalVideoSource() {
        return this.localOrOnlineVideo == Constants.VIDEO_SOURCE_LOCAL;
    }

    public String getTitle() {
        return title;
    }
}
