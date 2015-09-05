package com.lorem_ipsum.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by originally.us on 4/17/14.
 * <p/>
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 */
public class ImageUtils {

    private static final String LOG_TAG = "ImageUtils";


    public static Bitmap decodeBitmapFromImageUri(Context context, Uri imageUri, int reqWidth, int reqHeight) {
        String imagePath = FileUtils.getRealPathFromURI(context, imageUri);
        if (StringUtils.isNotNull(imagePath)) {
            return decodeBitmapFromImagePath(imagePath, reqWidth, reqHeight);
        }
        return null;
    }

    public static Bitmap decodeBitmapFromImagePath(String imagePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap sourceBitmap = BitmapFactory.decodeFile(imagePath, options);

        int rotationRadius = getRotationRadius(imagePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationRadius);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }

    public static BitmapFactory.Options decodeInJustDecodeBounds(Context context, Uri imageUri) {
        String imagePath = FileUtils.getRealPathFromURI(context, imageUri);
        if (StringUtils.isNotNull(imagePath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            return options;
        }
        return null;
    }

    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // check orientation and return radius of rotation
    private static int getRotationRadius(String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            switch (orientation) {
                case 3:
                    return 180;

                case 6:
                    return 90;

                case 8:
                    return -90;

                default:
                    return 0;
            }

        } catch (IOException e) {
            Log.w(LOG_TAG, "getRotationRadius error: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Resize
     */
    public static Uri resizeImage(Context context, Uri imageUri, int maxPixel) {
        Bitmap sourceBitmap = decodeBitmapFromImageUri(context, imageUri, maxPixel, maxPixel);
        if (sourceBitmap == null)
            return null;

        //get the original width and height
        int inputWidth = sourceBitmap.getWidth();
        int inputHeight = sourceBitmap.getHeight();

        if (inputWidth <= maxPixel && inputHeight <= maxPixel)
            return imageUri;

        Point p;
        int outputWidth;
        int outputHeight;

        if (inputWidth <= inputHeight) {
            p = getScaleDimen(inputWidth, inputHeight, maxPixel);
            outputWidth = p.x;
            outputHeight = p.y;
        } else {
            p = getScaleDimen(inputHeight, inputWidth, maxPixel);
            outputWidth = p.y;
            outputHeight = p.x;
        }

        Bitmap dstBitmap = Bitmap.createScaledBitmap(sourceBitmap, outputWidth, outputHeight, false);

        return saveImageFile(context, dstBitmap);
    }

    private static Point getScaleDimen(int inputWidth, int inputHeight, int maxPixel) {

        int outputWidth = 0;
        int outputHeight = 0;

        if (inputHeight <= maxPixel) {
            outputWidth = inputWidth;
            outputHeight = inputHeight;

        } else if (inputHeight > maxPixel) {
            outputWidth = maxPixel * inputWidth / inputHeight;
            outputHeight = maxPixel;

        }

        Point p = new Point();
        p.x = outputWidth;
        p.y = outputHeight;
        return p;
    }

    /**
     * Create image file from bitmap
     */
    private static Uri saveImageFile(Context context, Bitmap bitmap) {
        File pictureFile = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            LogUtils.logInDebug(LOG_TAG, "Error creating media file, check storage permissions");
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            return getImageContentUri(context, pictureFile);

        } catch (FileNotFoundException e) {
            LogUtils.logInDebug(LOG_TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            LogUtils.logInDebug(LOG_TAG, "Error accessing file: " + e.getMessage());
        }

        return null;
    }

    /**
     * get uri format: content://media/external/images/media/xxxxx
     */
    private static Uri getImageContentUri(Context context, File imageFile) {

        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");

            return Uri.withAppendedPath(baseUri, "" + id);

        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Crop normal bitmap to square bitmap and rotate
     */
    public static Uri saveBitmapWithOptimizeRotation(Context context, Bitmap sourceBitmap, float degreesRotate) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degreesRotate);

        // TODO check memory
        bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

        return ImageUtils.saveImageFile(context, bitmap);
    }

    public static Uri saveBitmapWithOptimizeRotation(Context context, byte[] bitmapData, float degreesRotate) {
        // TODO check memory
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        return saveBitmapWithOptimizeRotation(context, originalBitmap, degreesRotate);
    }

    public static boolean isPicasaImage(Uri imageUri) {
        if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) // picasa
            return true;
        if (imageUri.toString().startsWith("content://com.google.android.apps.photos")) // photo in google plus)
            return true;
        else
            return false;
    }

    public interface OnDownloadExternalImageDone {
        public void onDone(Uri imageUri);
    }

    public static void downloadExternalImageInBackground(final Activity context, final Uri imageUri, final OnDownloadExternalImageDone callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // create directory
                File cacheDir;
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    cacheDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // If the device has an SD card
                else
                    cacheDir = context.getApplicationContext().getCacheDir(); // If no SD card
                if (!cacheDir.exists())
                    cacheDir.mkdirs();

                // create file
                final String path = "gush-" + System.currentTimeMillis() + ".jpg";
                File file = new File(cacheDir, path);

                try {
                    InputStream input;
                    // Download the file
                    if (ImageUtils.isPicasaImage(imageUri)) {
                        input = context.getContentResolver().openInputStream(imageUri);
                    } else {
                        input = new URL(imageUri.toString()).openStream();
                    }

                    OutputStream output = new FileOutputStream(file);
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    final Uri newUri = getImageContentUri(context, file);

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(newUri);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDone(null);
                    }
                });
            }
        }).start();
    }

}
