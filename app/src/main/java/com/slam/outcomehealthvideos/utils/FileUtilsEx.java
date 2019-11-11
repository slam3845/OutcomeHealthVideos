package com.slam.outcomehealthvideos.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by slam on 09/07/2019.
 */
public class FileUtilsEx {
    static private final String TAG = FileUtilsEx.class.getSimpleName();

    /**
     * Read the the local text file which is stored in the asset folder
     * (e.g. "raw/video.txt")
     *
     * @param ctx
     * @param resId
     * @return
     */
    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int numBytes;
        try {
            numBytes = inputStream.read();
            while (numBytes != -1)
            {
                byteArrayOutputStream.write(numBytes);
                numBytes = inputStream.read();
            }
            inputStream.close();
        }
        catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }
}
