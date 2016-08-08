package com.jyo.android.eternalfriend.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
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
    /**
     * Use for store image in portrait
     *
     * @param bmImage image to rotate
     * @param file    where is stored the image
     * @return rotated byte array of the image
     * @throws IOException when can't write again the file
     */
    public static Bitmap portraitRotation(@NonNull Bitmap bmImage, @NonNull File file)
            throws IOException {

        ExifInterface exif = new ExifInterface(file.toString());

        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
            bmImage = rotate(bmImage, 90);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
            bmImage = rotate(bmImage, 270);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
            bmImage = rotate(bmImage, 180);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
            bmImage = rotate(bmImage, 90);
        }

        return bmImage;
    }

    /**
     * Rotates Bitmap image
     *
     * @param image  image to rotate
     * @param degree anti - clockwise degrees
     * @return image rotated
     */
    public static Bitmap rotate(@NonNull Bitmap image, int degree) {
        int width = image.getWidth();
        int height = image.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(degree);

        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
    }
}
