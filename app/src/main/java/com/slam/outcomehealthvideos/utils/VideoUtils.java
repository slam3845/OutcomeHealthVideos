package com.slam.outcomehealthvideos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.slam.outcomehealthvideos.Constants;
import com.slam.outcomehealthvideos.ThisApp;

import java.io.File;
import java.util.HashMap;

/**
 * Created by slam on 09/07/2019.
 */
public class VideoUtils {
    static private final String TAG = VideoUtils.class.getSimpleName();

    /**
     * For online video URL:
     *
     * @param urlVideo (e.g. "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
     * @return
     */
    static public long getOnlineVideoDuration(String urlVideo) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(urlVideo, new HashMap<String, String>());

        long timeInMillisec = retrieveTimeDuration(retriever);
        retriever.release();

        // String videoDuration = convertMillieToHMmSs(timeInMillisec);
        return timeInMillisec;
    }

    /**
     *
     * @param uriVideoSource - video file in local package "raw" folder.
     * @return
     */
    /**
     *
     * @param resourceFolder - local package resource folder (e.g. "raw")
     * @param uriVideoSource - video file in local package resourceFolder folder.
     * @return
     */
    static public Uri getLocalVideoFileUri(String resourceFolder, String uriVideoSource) {
        String packageName = ThisApp.getContext().getPackageName();
        int idRaw = ThisApp.getContext().getResources().getIdentifier(uriVideoSource,  resourceFolder, packageName);

        // String videoFile = "android.resource://" + packageName + "/" + R.raw.lets_meet_to_eat;
        String videoFile = "android.resource://" + packageName + "/" + idRaw;

        // Example: "android.resource://com.slam.outcomehealthvideos/raw/lets_meet_to_eat"
        //
        Uri localVideoFileUri = Uri.parse("android.resource://" + packageName + "/" + resourceFolder + "/" + uriVideoSource);
        return localVideoFileUri;
    }

    /**
     * Uri videoUri = Uri.parse("android.resource://" + packageName +"/raw/R.raw.big_buck_bunny");
     *
     * For local - "big_buck_bunny" on local asset folder "raw" without file extension:
     *    String packageName = ThisApp.getContext().getPackageName();
     *    Uri videoUri = Uri.parse("android.resource://" + packageName +"/raw/big_buck_bunny");
     *    long strDuration = VideoUtils.getLocalVideoDuration(videoUri);
     *
     * @param videoUri (e.g. "big_buck_bunny" on local asset folder "raw" without file extension)
     * @return
     */
    static public long getLocalVideoDuration(Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(ThisApp.getContext(), videoUri);

        long timeInMillisec = retrieveTimeDuration(retriever);
        retriever.release();

        // String videoDuration = convertMillieToHMmSs(timeInMillisec);
        return timeInMillisec;
    }

    /**
     *
     * @param retriever
     * @return
     */
    static private long retrieveTimeDuration(MediaMetadataRetriever retriever) {
        String timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        if (timeString != null) {
            long timeInMillisec = Long.parseLong(timeString);
            return timeInMillisec;
        }
        return 0;
    }


    /**
     *
     * @param frameAtTimeInMilliSeconds - in milliseconds
     * @param videoPath
     * @return
     * @throws Throwable
     */
    public static Bitmap retriveVideoFrameFromVideo(
        long frameAtTimeInMilliSeconds, String videoPath, boolean isLocalVideo
    ) {// throws Throwable
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            long frameAtTimeInMicroseconds = 0;
            mediaMetadataRetriever = new MediaMetadataRetriever();

            if (isLocalVideo) {
                Context context = ThisApp.getContext();
                frameAtTimeInMicroseconds = frameAtTimeInMilliSeconds * 1000;

                Uri localVideoFileUri = getLocalVideoFileUri("raw", videoPath);
                mediaMetadataRetriever.setDataSource(context, localVideoFileUri);
            }
            else {
                if (Build.VERSION.SDK_INT >= 14) {
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                } else {
                    mediaMetadataRetriever.setDataSource(videoPath);
                }
            }

            //  Instead of milliseconds, so we need to convert milliseconds to microseconds here...
            //
            frameAtTimeInMicroseconds = frameAtTimeInMilliSeconds * 1000;
            int byteCount = 0;
            do {
                // Important Note:
                //      MediaMetadataRetriever's getFrameAt method takes in microseconds (1/1,000,000th of a second)
                //
                bitmap = mediaMetadataRetriever.getFrameAtTime(frameAtTimeInMicroseconds, MediaMetadataRetriever.OPTION_CLOSEST);
                frameAtTimeInMicroseconds += 1000000;
                byteCount = bitmap.getByteCount();
            }
            while ((bitmap == null || byteCount == 0) && frameAtTimeInMicroseconds < 10000000);
        }
        catch (Exception ex) {
            Log.e(TAG, "retriveVideoFrameFromVideo(long frameAtTime, String videoPath) Exception: " + ex.getMessage());
            ex.printStackTrace();
            // throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ ex.getMessage());
        }
        finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        if (bitmap.getHeight() > Constants.THUMBNAIL_HEIGHT) {
            bitmap.setHeight(Constants.THUMBNAIL_HEIGHT);
        }
        return bitmap;
    }

    /**
     * Glide https://futurestud.io/tutorials/glide-displaying-gifs-and-videos
     *
     * @param filePath (e.g. "/storage/emulated/0/Pictures/example_video.mp4";
     * @return
     */
    static public void glideGetThumbnailFromVideo(Context context, String filePath, ImageView imageView) {
        Glide
            .with(context)
            .asBitmap()
            .load(Uri.fromFile(new File(filePath)))
            .into(imageView);    // imageViewGifAsBitmap
    }

    /**
     * DOES NOT WORK!!!
     *
     * @param context
     * @param urlVideoFile
     * @param imageView
     */
    static public void getThumbnailFromVideo(Context context, String urlVideoFile, ImageView imageView) {
        if (urlVideoFile == null || urlVideoFile.isEmpty()) {
            return;
        }

        Bitmap bitmapThumb = null;
        try {
            // bitmapThumb is null
            bitmapThumb = ThumbnailUtils.createVideoThumbnail(
                urlVideoFile, MediaStore.Video.Thumbnails.MINI_KIND
            );// MINI_KIND, size: 512 x 384 thumbnail | MICRO_KIND, size: 96 x 96 thumbnail

//          ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(bitmapThumb);
/*
            Uri uri = getImageUri(context, bitmapThumb);

            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.id.ivVideoThumbnail)// R.drawable.ic_imagedefault
                    .error(R.id.ivVideoThumbnail)// R.drawable.ic_imagedefault
                    .resize(350, 200)//as per need.
                    .centerCrop()
                    .into(imageView);
*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bitmapThumb = null;
        }
    }
}
