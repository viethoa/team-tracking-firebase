package com.lorem_ipsum.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by DangTai on [15 March 2014].
 */
public final class FileUtils {

    private static final String LOG_TAG = "FileUtils";

    public static final int MEDIA_TYPE_IMAGE = 1;

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static void deleteFile(Context context, Uri uri) {
        if (uri == null) {
            return;
        }

        // delete
        context.getContentResolver().delete(uri, null, null);

        // refresh gallery data
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    public static String readTextFileFromAsset(AssetManager assetManager, String pathFile) {
        try {
            InputStream is = assetManager.open(pathFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = "";
            StringBuffer buf = new StringBuffer();
            str = reader.readLine();
            while (str != null) {
                buf.append(str + "\n");
                str = reader.readLine();
            }
            reader.close();
            is.close();
            String data = buf.toString();
            //Log.w("READ DATA", data);
            return data;
        } catch (FileNotFoundException e) {
            LogUtils.logInDebug(LOG_TAG, "readTextFileFromAsset() File not found: " + e.getMessage());
        } catch (IOException e) {
            LogUtils.logInDebug(LOG_TAG, "readTextFileFromAsset() IO error: " + e.getMessage());
        }
        return null;
    }

    public static String readTextFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fin = new FileInputStream(filePath);
            if (fin != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                fin.close();
            }

        } catch (Exception e) {
            LogUtils.logErrorDebug(LOG_TAG, "readTextFile error: " + e.getMessage());

        }
        return builder.toString();
    }

    public static void unZip(String zipFilePath, String unZipPath) {
        //Sanity check
        if (!unZipPath.endsWith("/")) {
            unZipPath += "/";
        }

        ZipInputStream zin = null;
        try {
            FileInputStream fin = new FileInputStream(zipFilePath);
            zin = new ZipInputStream(fin);
            ZipEntry entry;

            //Read entries in zip file
            while ((entry = zin.getNextEntry()) != null) {
                LogUtils.logInDebug(LOG_TAG, "unzipping " + entry.getName() + "  length " + entry.getSize());

                if (entry.isDirectory()) {
                    File f = new File(unZipPath + entry.getName());
                    if (!f.isDirectory())
                        f.mkdirs();

                } else {
                    //Write this entry to file
                    FileOutputStream fout = new FileOutputStream(unZipPath + entry.getName());
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, length);
                    }

                    fout.flush();
                    fout.close();
                    zin.closeEntry();
                }
            }

        } catch (Exception e) {
            LogUtils.logErrorDebug(LOG_TAG, "unzip error: " + e.getMessage());

        } finally {
            try {
                if (zin != null)
                    zin.close();
            } catch (IOException e) {
                LogUtils.logErrorDebug(LOG_TAG, "unzip error: " + e.getMessage());
            }
        }
    }

    public static File getPublicDownloadDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

}
