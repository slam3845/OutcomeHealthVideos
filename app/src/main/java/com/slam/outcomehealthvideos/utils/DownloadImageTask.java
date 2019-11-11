package com.slam.outcomehealthvideos.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.slam.outcomehealthvideos.Constants;

import java.io.InputStream;

/**
 * Created by slam on 09/07/2019.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    static private final String TAG = DownloadImageTask.class.getSimpleName();

    ImageView _bmpImage;
    Bitmap _bmpThumbNailCache;

    public DownloadImageTask(ImageView bmpImage, Bitmap bmpThumbNailCache) {
        this._bmpImage = bmpImage;
        this._bmpThumbNailCache = bmpThumbNailCache;
    }

    /**
     * Download the image in an ASyncTask.
     *
     * @param urls
     * @return
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (Exception ex) {
            Log.e(TAG,"doInBackground() Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap.getHeight() > Constants.THUMBNAIL_HEIGHT) {
            bitmap.setHeight(Constants.THUMBNAIL_HEIGHT);
        }
        _bmpImage.setImageBitmap(bitmap);
        _bmpThumbNailCache = bitmap;
    }
}
