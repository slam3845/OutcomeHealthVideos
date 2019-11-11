package com.slam.outcomehealthvideos.ui.adapters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.slam.outcomehealthvideos.Constants;
import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.ThisApp;
import com.slam.outcomehealthvideos.data.VideoData;
import com.slam.outcomehealthvideos.ui.main.MainFragment;
import com.slam.outcomehealthvideos.utils.DateTimeUtils;
import com.slam.outcomehealthvideos.utils.DownloadImageTask;
import com.slam.outcomehealthvideos.utils.VideoUtils;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link VideoData} and makes a call to the
 * specified {@link MainFragment.OnListFragmentInteractionListener}.
 *
 * Created by slam on 09/05/2019.
 */
public class VideoItemRecyclerViewAdapter extends RecyclerView.Adapter<VideoItemRecyclerViewAdapter.ViewHolder> {
    static private final String TAG = VideoItemRecyclerViewAdapter.class.getSimpleName();

    /**
     * The video list to populate in the gallery list view.
     */
    private final List<VideoData> _listVideoData;
    private ViewGroup _parent;


    /**
     *
     * @param listVideoData
     */
    public VideoItemRecyclerViewAdapter(List<VideoData> listVideoData) {
        this._listVideoData = listVideoData;
    }

    /**
     *  Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type
     *  to represent an item.
     *
     *  The RecyclerView’s adapter uses the ViewHolder pattern. ViewHolder Allow making
     *  list scrolling act smoothly.  It store list row views references with calling the
     *  findViewById() method only occurs a couple of times, rather than for the entire
     *  dataset and on each bind view.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        _parent = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the
     * item at the given position.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.videoData = _listVideoData.get(position);

        // The video "sources" in the videos.json file could have multiple video URLs.
        // But to make it simple for this demo, we're only interested in the first video.
        //
        VideoData.VideoSourceAndDuration videoSourceAndDuration = holder.videoData.listUrlVideoSourceAndDuration.get(0);
        boolean isLocalVideo = (holder.videoData.localOrOnlineVideo == Constants.VIDEO_SOURCE_LOCAL);

        if (ThisApp.shouldUseCachedThumbnailImages && holder.videoData.bmpThumbNailCache != null) {
            holder.ivThumbNail.setImageBitmap(holder.videoData.bmpThumbNailCache);
        }
        else {
            // Thumbnail image has not been loaded yet.
            if (ThisApp.shouldRetrieveThumbnailFromVideo || isLocalVideo) {
                // Get a picture frame from the video
                String urlVideoSource = holder.videoData.listUrlVideoSourceAndDuration.get(0).urlVideoSource;

                if (holder.videoData.bmpThumbNailCache == null || !ThisApp.shouldUseCachedThumbnailImages) {
                    // This call does not work!
                    // VideoUtils.getThumbnailFromVideo(_parent.getContext(), urlVideoSource,  holder.ivThumbNail);

                    Bitmap bitmap = VideoUtils.retriveVideoFrameFromVideo(ThisApp.videoThumbnailAtTimeInMilliSeconds, urlVideoSource, isLocalVideo);
                    holder.ivThumbNail.setImageBitmap(bitmap);
                    holder.videoData.bmpThumbNailCache = bitmap;
                }
            } else {
                // Retrieve thumbnail from explicit thumbnail URL.
                //
                if (holder.videoData.urlThumbNail == null || holder.videoData.urlThumbNail.isEmpty()) {
                    holder.ivThumbNail.setImageResource(R.drawable.ic_video_reel);
                } else {
                    // Download the image from the thumbnail url
                    new DownloadImageTask(holder.ivThumbNail, holder.videoData.bmpThumbNailCache).execute(holder.videoData.urlThumbNail);
                }
            }
        }

        if (ThisApp.shouldRetrieveVideoTimeDuration) {
            getVideoTimeDuration(holder.videoData);
        }
        holder.tvDuration.setText(videoSourceAndDuration.strDuration);
        holder.tvTitle.setText(holder.videoData.title);
        holder.tvSubTitle.setText(holder.videoData.subTitle);
        holder.tvDescription.setText(holder.videoData.description);

        /*
         * Set the layout components' tag to store the VideoData object.
         * The tag will be retrieved in MainActivity.onVideoItemClicked() when the component is clicked
         *
         * (See the components which has the android:onClick="onVideoItemClicked")
         */
        holder.layoutThumbnail.setTag(holder.videoData);
        holder.llTextArea.setTag(holder.videoData);
        holder.ivThumbNail.setTag(holder.videoData);
        holder.tvTitle.setTag(holder.videoData);
        holder.tvSubTitle.setTag(holder.videoData);
        holder.tvDescription.setTag(holder.videoData);
    }

    /**
     * Get the time duration from the video clip.
     *
     * Example of "urlVideoSource"
     *  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
     *  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
     *
     * @param videoData
     * @return
     */
    public String getVideoTimeDuration(VideoData videoData) {
        if (videoData == null) {
            return "";
        }

        // The video "sources" in the videos.json file could have multiple video URLs.
        // But to make it simple for this demo, we're only interested in the first video.
        //
        VideoData.VideoSourceAndDuration videoSourceAndDuration = videoData.listUrlVideoSourceAndDuration.get(0);
        if (videoSourceAndDuration == null || videoSourceAndDuration.urlVideoSource == null) {
            return "";
        }
        if (! videoSourceAndDuration.strDuration.isEmpty()) {
            // The time duration has already been fetch, so there is no need to do it again.
            return videoSourceAndDuration.strDuration;
        }

        if (videoData.isOnlineVideoSource()) {
            videoSourceAndDuration.lDuration = VideoUtils.getOnlineVideoDuration(videoSourceAndDuration.urlVideoSource);
        }
        else {
            Uri videoUri = VideoUtils.getLocalVideoFileUri("raw", videoSourceAndDuration.urlVideoSource);
            videoSourceAndDuration.lDuration = VideoUtils.getLocalVideoDuration(videoUri);
        }
        videoSourceAndDuration.strDuration = DateTimeUtils.convertMillieToHMmSs(videoSourceAndDuration.lDuration);
        return videoSourceAndDuration.strDuration;
    }

    @Override
    public int getItemCount() {
        return _listVideoData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View layoutThumbnail;
        public final View llTextArea;

        public final ImageView ivThumbNail;
        public final TextView tvDuration;
        public final TextView tvTitle;
        public final TextView tvSubTitle;
        public final TextView tvDescription;
        public VideoData videoData;

        /**
         * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            layoutThumbnail = view.findViewById(R.id.rlThumbnail);
            tvDuration = view.findViewById(R.id.tvDuration);
            llTextArea = view.findViewById(R.id.llTextArea);
            ivThumbNail = view.findViewById(R.id.ivVideoThumbnail);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvSubTitle = view.findViewById(R.id.tvSubTitle);
            tvDescription = view.findViewById(R.id.tvDescription);

            view.findViewById(R.id.layoutVideoItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Video item clicked: ");
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvTitle.getText() + "';  '" + tvSubTitle.getText() + "'";
        }
    }
}
