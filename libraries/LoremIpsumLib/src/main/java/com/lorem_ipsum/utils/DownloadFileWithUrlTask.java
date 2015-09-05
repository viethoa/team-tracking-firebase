package com.lorem_ipsum.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by originally.us on 7/21/14.
 */
public class DownloadFileWithUrlTask extends AsyncTask<String, Integer, String> {

    private final String LOG_TAG = "DownloadFileWithUrlTask";

    private Context mContext;

    public DownloadFileWithUrlTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String strUrl = params[0];

        // sdcard/Android/data/<pagkagename>/cache/<filename>
        File cacheDir = AppUtils.getAppContext().getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = AppUtils.getAppContext().getCacheDir();
        }
        String filePath = cacheDir.getAbsolutePath() + "/" + System.currentTimeMillis();

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return "error: Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

            // this will be useful to display download percentage, might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(filePath);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return "error: Cancelled";
                }
                total += count;

                // publishing the progress....
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));

                output.write(data, 0, count);
            }

            output.flush();
            return filePath;

        } catch (Exception e) {
            return "error: " + e.toString();

        } finally {
            try {
                if (output != null)
                    output.close();

                if (input != null)
                    input.close();

            } catch (IOException e) {
                LogUtils.logInDebug(LOG_TAG, "error: " + e.getMessage());
            }

            if (connection != null)
                connection.disconnect();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mDownloadTaskListenner != null)
            mDownloadTaskListenner.onPreStartDownload();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (mDownloadTaskListenner != null)
            mDownloadTaskListenner.onUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mDownloadTaskListenner == null)
            return;

        if (StringUtils.isNull(result))
            mDownloadTaskListenner.onFailure("null");
        else if (result.contains("error"))
            mDownloadTaskListenner.onFailure(result);
        else
            mDownloadTaskListenner.onSuccess(result);
    }


    //************************************************************************
    // Listenner interface
    //************************************************************************

    private DownloadTaskListenner mDownloadTaskListenner;

    public interface DownloadTaskListenner {
        public void onPreStartDownload();

        public void onUpdate(int percent);

        public void onSuccess(String filePath);

        public void onFailure(String errorMessage);
    }

    public void setListenner(DownloadTaskListenner listenner) {
        mDownloadTaskListenner = listenner;
    }
}
