package com.slam.outcomehealthvideos.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slam.outcomehealthvideos.Constants;
import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.ThisApp;
import com.slam.outcomehealthvideos.data.VideoData;
import com.slam.outcomehealthvideos.ui.activities.MainActivity;
import com.slam.outcomehealthvideos.ui.adapters.VideoItemRecyclerViewAdapter;
import com.slam.outcomehealthvideos.utils.DeviceUtils;
import com.slam.outcomehealthvideos.utils.FileUtilsEx;
import com.slam.outcomehealthvideos.utils.VideoDataParser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by slam on 09/05/2019.
 */
public class MainFragment extends Fragment {
    static private final String TAG = MainFragment.class.getSimpleName();

    static private final String KEY_INDEX_VIDEO_ITEM_PLAYING = "KeyIndexVideoItemPlaying";
    static private final String KEY_VIDEO_POSTION = "KeyVideoPosition";

    private VideoItemRecyclerViewAdapter _videoItemRecyclerViewAdapter = null;
    private OnListFragmentInteractionListener _listFragmentInteractionListener;
    private MainViewModel _mainViewModel;

    private VideoView _videoView;
    private View _viewMainFragment;
    private List<VideoData> _listVideoData = null;

    private int _indexOfVideoPlaying = -1;
    private int _videoPosition = 0;


    /**
     * This interface must be implemented by activities that contain this fragment to
     * allow an interaction in this fragment to be communicated to the activity and
     * potentially other fragments contained in that activity.
     * <p/>
     * See the Android Training lesson
     * <a href="http://developer.android.com/training/basics/fragments/communicating.html">
     * Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(VideoData videoData);
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For this demo, we assume the video list will not change.
        // So we only need to parse the video json file once.
        //
        if (_listVideoData == null || _listVideoData.size() == 0) {
            String jsonVideoData = FileUtilsEx.readRawTextFile(getActivity(), R.raw.videos);
            VideoDataParser videoDataParser = new VideoDataParser();
            _listVideoData = videoDataParser.parse(jsonVideoData);
        }
        _listFragmentInteractionListener = (OnListFragmentInteractionListener)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        _viewMainFragment = inflater.inflate(R.layout.fragment_video_main, container, false);

        resizePortraitLayoutsForScreenSize();

        View listView = _viewMainFragment.findViewById(R.id.listViewVideos);

        // Set the adapter
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();    // MainActivity object
            RecyclerView recyclerView = (RecyclerView) listView;

            // recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            _videoItemRecyclerViewAdapter = new VideoItemRecyclerViewAdapter(_listVideoData);
            recyclerView.setAdapter(_videoItemRecyclerViewAdapter);
        }
        return _viewMainFragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        _videoView.seekTo(_videoPosition);
        _videoView.start();
    }

    /**
     * Change device orientation (Portrait <=> Landscape), onPause() will be called prior to
     * onSaveInstanceState(Bundle outState).
     *
     * VideoView.getCurrentPosition() will return correct value inside onPause().
     * But on some device (e.g. Samsung Note 4 with Android API 6.0.1 Marshmallow), it will
     * always return 0.  Therefore, to overcome this problem, we set the position here.
     */
    @Override
    public void onPause() {
        super.onPause();
        _videoPosition = _videoView.getCurrentPosition();
        Log.d(TAG, "MainFragment.onPause(): _videoPosition=" + _videoPosition);
    }

    @Override
    public void onStop() {
        super.onStop();
        _videoView.pause();
    }

    /**
     * IMPORTANT:
     * On Samsung device running Android API 6.01 (Marshmallow), calling VideoView.getCurrentPosition()
     * inside onSaveInstanceState(Bundle outState) will return the correct value.
     *
     * But on Google Pixel 3 device (and others?) calling VideoView.getCurrentPosition() inside
     * onSaveInstanceState(Bundle outState) will ALWAYS return 0!!!  Maybe the video has already reset?
     *
     * Instead, calling VideoView.getCurrentPosition() inside onPause() will return the desired value.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // On some device (e.g. Samsung Note 4 with Android API 6.0.1 Marshmallow),
        // videoView.getCurrentPosition() will always return 0 when call inside
        // onSaveInstanceState().
        // However, call inside onPause() will return the correct value.
        // Calling sequence: onPause() => ... => onSaveInstanceState().
        //
        int thisVideoPosition = _videoView.getCurrentPosition();

        outState.putInt(KEY_VIDEO_POSTION, _videoPosition);
        outState.putInt(KEY_INDEX_VIDEO_ITEM_PLAYING, _indexOfVideoPlaying);
        Log.d(TAG, "MainFragment.onSaveInstanceState(): _videoPosition=" + _videoPosition);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            _indexOfVideoPlaying = savedInstanceState.getInt(KEY_INDEX_VIDEO_ITEM_PLAYING);
            _videoPosition = savedInstanceState.getInt(KEY_VIDEO_POSTION);
            selectVideo(_indexOfVideoPlaying);
        }
    }

    private void resizePortraitLayoutsForScreenSize() {
        Configuration configInfo = getResources().getConfiguration();
        if (configInfo.orientation == Configuration.ORIENTATION_PORTRAIT) {

            /**
             * At runtime, set the height of both the video player (1/3) and video list view (2/3)
             * accordingly for different Android device size/resolution.
             */
            int screenHeight = DeviceUtils.getDeviceDisplayHeightInPixels(getActivity());

            // Set the height of the VideoView to 1/3 of the screen height
            LinearLayout layoutCardViewVideo = _viewMainFragment.findViewById(R.id.layoutCardViewVideo);
            ViewGroup.LayoutParams paramsCardView = layoutCardViewVideo.getLayoutParams();
            paramsCardView.height = (int)(screenHeight / 3.0);
            layoutCardViewVideo.setLayoutParams(paramsCardView);

            // Set the height of the video RecycleView to take up the remaining screen height.
            LinearLayout layoutListViewVideos = _viewMainFragment.findViewById(R.id.layoutListViewVideos);
            ViewGroup.LayoutParams paramsListViewVideos = layoutListViewVideos.getLayoutParams();
            paramsListViewVideos.height = screenHeight - paramsCardView.height;
            layoutListViewVideos.setLayoutParams(paramsListViewVideos);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.d(TAG, "onConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE)");

                // Display the video in full screen.
                android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) _videoView.getLayoutParams();
                params.width = DeviceUtils.getDeviceDisplayWidthInPixels(getActivity()) + 100;
                params.height = DeviceUtils.getDeviceDisplayHeightInPixels(getActivity()) + 100;
                _videoView.setLayoutParams(params);
                Constants.THUMBNAIL_HEIGHT = params.height / 3;
            }
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "onConfigurationChanged(Configuration.ORIENTATION_PORTRAIT)");
                android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) _videoView.getLayoutParams();
                params.width = DeviceUtils.getDeviceDisplayWidthInPixels(getActivity()) + 100;
                params.height = DeviceUtils.getDeviceDisplayHeightInPixels(getActivity()) + 100;

                _videoView.setLayoutParams(params);
                Constants.THUMBNAIL_HEIGHT = params.height / 6;
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "onConfigurationChanged() Exception caught: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        /***********************************************************************
         * Observer:
         *
         * When the VideoData (LiveData) is updated with new video to play, this
         * observer will automatically call with the new videoData.
         *
         * See MainViewModel.setVideoData(VideoData videoData)
         */
        final Observer<VideoData> observerVideoData = new Observer<VideoData>() {
            @Override
            public void onChanged(@Nullable final VideoData videoData) {
                if (videoData == null) {
                    return;
                }
                Log.d(TAG, "observeVideoLiveDataChange.onChange(): " );

                // The video "sources" in the videos.json file could have multiple video URLs.
                // But to make it simple for this demo, we're only interested in the first video.
                //
                VideoData.VideoSourceAndDuration videoSourceAndDuration = videoData.listUrlVideoSourceAndDuration.get(0);
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity)activity).setAppTitleText(videoData.getTitle());
                }
                if (videoData.isOnlineVideoSource()) {
                    // Example of online video url:
                    //      https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
                    //
                    _videoView.setVideoURI(Uri.parse(videoSourceAndDuration.urlVideoSource));
                }
                else {
                    // Video file stored on local asset raw folder.
                    // Example of local video name: "big_buck_bunny"
                    //
                    playLocalVideoClip(videoSourceAndDuration.urlVideoSource);
                }
                _videoView.seekTo(_videoPosition);
                _videoView.start();
            }
        };

        // Observe the VideoData changes.
        _mainViewModel.getVideoData().observe(this, observerVideoData);

        _indexOfVideoPlaying = 0;
        _mainViewModel.setVideoData(_listVideoData.get(_indexOfVideoPlaying));

        setupVideoMediaPlayer();
    }

    /**
     *
     * @param urlVideoSource (e.g. "big_buck_bunny")
     */
    private void playLocalVideoClip(String urlVideoSource) {
        String packageName = ThisApp.getContext().getPackageName();
        int idRaw = ThisApp.getContext().getResources().getIdentifier(urlVideoSource,  "raw", packageName);

        // String videoFile = "android.resource://" + packageName + "/" + R.raw.lets_meet_to_eat;
        String videoFile = "android.resource://" + packageName + "/" + idRaw;
        Uri videoUri = Uri.parse("android.resource://" + packageName +"/raw/lets_meet_to_eat");
        _videoView.setVideoURI(videoUri);
    }

    public void setupVideoMediaPlayer(){
        // Previous version:
        // Launch the video clip in the default separate video player app.
        //
        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.tutorialVideoURL)));

        // New version:
        // Launch the video clip in the embedded <VideoView> control inside the same activity.
        //
        _videoView = (VideoView) _viewMainFragment.findViewById(R.id.videoView);

        final MediaController mediaController = new MediaController(getContext());

        //  mediaController.setMediaPlayer(_videoView);
        mediaController.setAnchorView(_videoView);
        _videoView.setMediaController(mediaController);

        // The video to play will be set in MainFragment.onActivityCreated() where the
        // the MainViewModel is setup to observe the VideoData(LiveData) data changes
        // and the "(Observer<VideoData>).onChanged()" will be invoked where we will
        // set the video url to play.
        //
        // _videoView.setVideoPath(urlVideo);
        // _videoView.setVideoURI(Uri.parse(urlVideo));

//        _videoView.seekTo(_videoPosition);
        _videoView.bringToFront();
        _videoView.setBackgroundColor(Color.TRANSPARENT);    // On some device, VideoView does not show video ????
        _videoView.requestFocus();
        _videoView.setVisibility(View.VISIBLE);
        _videoView.start();
        _videoView.setZOrderOnTop(true);

        // video finish listener
        _videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        _videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mediaController != null) {
                        if (mediaController.isShowing()) {
                            mediaController.hide();
                        } else {
                            mediaController.show();
                        }
                    }
                }
                return true;
            }
        });

        // video finish listener
        _videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // On ending, select the next video to play.
                // If it is the last video in the list, it will loop back to the first video.
                _indexOfVideoPlaying = ++_indexOfVideoPlaying % _listVideoData.size();
                _videoPosition = 0;
                selectVideo(_indexOfVideoPlaying);
                Log.d(TAG, ">>> Next Video: " + _listVideoData.get(_indexOfVideoPlaying).title);
            }
        });
    }

    public List<VideoData> getListOfVideoData () {
        return _listVideoData;
    }

    public VideoData getVideoData(int indexVideo) {
        VideoData videoData = _listVideoData.get(indexVideo);
        return videoData;
    }

    public void selectVideo(int item) {
        VideoData videoData = _listVideoData.get(item);
        _mainViewModel.setVideoData(videoData);
        _listFragmentInteractionListener.onListFragmentInteraction(videoData);
    }

    public void selectVideo(VideoData videoData) {
        _indexOfVideoPlaying = videoData.indexVideo;
        _mainViewModel.setVideoData(videoData);
        _listFragmentInteractionListener.onListFragmentInteraction(videoData);
    }

    public void notifyDataSetChanged() {
        for (VideoData videoData : _listVideoData) {
            // Settings has changed (either one or both):
            //  - Cache thumbnail images;
            //  - Retrieve video from video or Thumbnail url
            videoData.bmpThumbNailCache = null;
        }
        _videoItemRecyclerViewAdapter.notifyDataSetChanged();
    }
}
