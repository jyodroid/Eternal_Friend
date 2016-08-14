package com.jyo.android.eternalfriend.commons;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
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
    private static final String FILE_PROVIDER_AUTHORITY = "com.jyo.android.eternalfriend.fileprovider";
    public static final int REQUEST_TAKE_PICTURE = 1;
    public static final int REQUEST_OBTAIN_FROM_GALLERY = 2;

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

    /**
     * Transform a {@link Bitmap} into an {@link Byte} array
     * @param photo
     * @return
     */
    public static byte[] bitmapToArray(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static void galleryAddPic(String filePath, Activity activity) throws IOException {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static String useCameraIntent(Activity activity, String prefix) throws Exception {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            photoFile = createImageFile(activity, prefix);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                String filePath = photoFile.getAbsolutePath();
                Uri photoURI =
                        FileProvider.getUriForFile(activity,
                                FILE_PROVIDER_AUTHORITY,
                                photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
                return filePath;
            }else {
                throw new Exception("Can't obtain image");
            }
        } else {
            throw new Exception("Can't access camera app");
        }
    }

    /**
     * Used to blur images using render script. The base blur radius is 7.5
     * @param context from where is called the function
     * @param original image to be blur
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Bitmap blurImage(Context context, Bitmap original){

        final float BITMAP_SCALE = 0.4f;
        final float BLUR_RADIUS = 7.5f;

        int width = Math.round(original.getWidth() * BITMAP_SCALE);
        int height = Math.round(original.getHeight() * BITMAP_SCALE);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);
        Bitmap blurBitmap = Bitmap.createBitmap(scaledBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, scaledBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, blurBitmap);
        intrinsicBlur.setRadius(BLUR_RADIUS);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(blurBitmap);

        return blurBitmap;
    }
}
