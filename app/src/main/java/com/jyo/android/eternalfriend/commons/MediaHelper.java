package com.jyo.android.eternalfriend.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JohnTangarife on 6/08/16.
 */
public class MediaHelper {

    private static final String FILE_NAME_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    private static final String APP_NAME = "Eternal Friend";

    public static void setPic(ImageView holder, String filePath) {
        // Get the dimensions of the View
        int targetW = holder.getWidth();
        int targetH = holder.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
        holder.setImageBitmap(bitmap);
    }

    public static File createImageFile(Context context, String prefix) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(FILE_NAME_DATE_FORMAT).format(new Date());
        String imageFileName = prefix + "_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File picturesDirectory = new File(storageDir, APP_NAME);

        // Create the storage directory if it does not exist
        if (!picturesDirectory.exists()) {
            if (!picturesDirectory.mkdirs()) {
                Log.d(APP_NAME, "failed to create directory");
                return null;
            }
        }
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",                 /* suffix */
                picturesDirectory      /* directory */
        );

        return image;
    }
}
