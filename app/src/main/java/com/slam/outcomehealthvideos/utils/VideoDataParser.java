package com.slam.outcomehealthvideos.utils;

import android.util.Log;

import com.slam.outcomehealthvideos.Constants;
import com.slam.outcomehealthvideos.data.VideoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slam on 09/06/2019.
 */
public class VideoDataParser {
    static private final String TAG = VideoDataParser.class.getSimpleName();

    /**
     * Parse the json data store in the file.
     *
     * @param jsonData
     * @return
     */
    public List<VideoData> parse(String jsonData) {
        List<VideoData> listVideos = new ArrayList<VideoData>();
        VideoData videoData = null;

        try {
            int idVideo = 0;
            JSONObject jsonDataObject = new JSONObject(jsonData);
            JSONArray jsonCategoryArray = jsonDataObject.optJSONArray("categories");
            int numVideoCategories = jsonCategoryArray.length();

            for (int iX = 0; iX < numVideoCategories; iX++) {
                JSONObject jsonCategoryObject = (JSONObject)jsonCategoryArray.get(iX);
                // String videoType = jsonCategoryObject.optString("name");

                /**
                 * VideosOnline
                 */
                JSONArray jsonVideosArray = jsonCategoryObject.optJSONArray("VideosOnline");
                int numVideos = jsonVideosArray.length();

                for (int jX = 0; jX < numVideos; jX++) {
                    videoData = getVideoFromJsonData(idVideo, Constants.VIDEO_SOURCE_ONLINE, (JSONObject)jsonVideosArray.get(jX));
                    listVideos.add(videoData);
                    idVideo++;
                }

                /**
                 * VideosLocal
                 */
                jsonVideosArray = jsonCategoryObject.optJSONArray("VideosLocal");
                numVideos = jsonVideosArray.length();

                for (int jX = 0; jX < numVideos; jX++) {
                    videoData = getVideoFromJsonData(idVideo, Constants.VIDEO_SOURCE_LOCAL, (JSONObject)jsonVideosArray.get(jX));
                    listVideos.add(videoData);
                    idVideo++;
                }
            }
        } catch (JSONException ex) {
            Log.e(TAG, "parse(jsonData) JSONException encountered! " + ex.getMessage());
            ex.printStackTrace();
        }
        return listVideos;
    }

    /*
     * Example of video json data:
     * {
     *    "categories":[
     *      {
     *          "VideosLocal": [
     *             {
     *                   "sources":"LetsMeetToEat.mp4",
     *                   "title": "LetsMeetToEat"
     *             }
     *          ],
     *
     *          "VideosOnline":[
     *             {
     *                "description":"Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself..."
     *                "sources":[
     *                   "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
     *                ],
     *                "subtitle":"By Blender Foundation",
     *                "thumb":"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
     *                "title":"Big Buck Bunny"
     *             },
     *          ]
     *       }
     *    ]
     * }
     */
    private VideoData getVideoFromJsonData(int idVideo, int onlineOrLocal, JSONObject jsonObjVideos) {
        VideoData videoData = new VideoData();

        try {
            videoData.indexVideo = idVideo;
            videoData.localOrOnlineVideo = onlineOrLocal;
            videoData.description = jsonObjVideos.optString("description");
            videoData.title = jsonObjVideos.optString("title");
            videoData.subTitle = jsonObjVideos.optString("subtitle");
            videoData.urlThumbNail = jsonObjVideos.optString("thumb");

            JSONArray arrSources = jsonObjVideos.optJSONArray("sources");
            for (int i = 0; i < arrSources.length(); i++) {
                // Example:
                //  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
                //  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
                //
                String urlVideo = (String)arrSources.get(i);
                videoData.addVideoSourceUrl(urlVideo);
            }
        } catch (JSONException ex) {
            return null;
        }
        return videoData;
    }
}
