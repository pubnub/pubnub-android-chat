package com.pubnub.chatterbox.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.pubnub.chatterbox.Constants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Frederick on 5/14/15.
 */
public class DownloadProfileImageTask extends AsyncTask<Void, Void, Boolean> {

    private String imageURL;
    private String userID;
    private ImageView imageViewRef;
    private BitmapDrawable drawable;
    private Context context;


    @Override
    protected Boolean doInBackground(Void... params) {


        FileOutputStream rawStream = null;
        InputStream imageStream = null;

        try {
            String baseContextPath = context.getFilesDir().getAbsolutePath();

            Log.d("DOWNLOADER", "Downloading image for task");


            File f = new File(baseContextPath + userID);
            if (f.exists()) {
                Log.d("DOWNLOADER", "path: " + f.getAbsolutePath() + " exists");
            } else {
                Log.d("DOWNLOADER", "path: " + f.getAbsolutePath() + " does NOT exists");
                Log.d(Constants.LOGT, "downloading url: " + imageURL);

                URL url = new URL(imageURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                int contentLength = httpConnection.getContentLength();
                String contentType = httpConnection.getContentType();
                imageStream = httpConnection.getInputStream();
                Log.d("DOWNLOADER", "content length: " + contentLength + " content type: " + contentType);

                rawStream = context.openFileOutput(f.getAbsolutePath(), Context.MODE_WORLD_READABLE);
                byte[] buffer = new byte[contentLength];
                imageStream.read(buffer, 0, buffer.length);
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                Bitmap bDrawable = BitmapFactory.decodeStream(bis);

                //Save the avatar
                boolean compressSuccess = bDrawable.compress(getCompressFormat(contentType), 100, rawStream);
                if (compressSuccess != false) {
                    Log.d("DOWNLOADER", "avatar bitmap compression is success");
                }


            }

        } catch (Exception e) {
            Log.e("DOWNLOADER", "exception while downloading avatar", e);
        } finally {
            if (rawStream != null) {
                try {
                    rawStream.close();
                } catch (IOException e) {
                }
            }

        }

        return true;
    }

    private Bitmap.CompressFormat getCompressFormat(String contentType) {
        if ((contentType.indexOf("jpg") > -1)
                || ((contentType.indexOf("jpeg") > -1))) {
            return Bitmap.CompressFormat.JPEG;
        } else if (contentType.indexOf("png") > -1) {
            return Bitmap.CompressFormat.PNG;
        }
        return (Bitmap.CompressFormat.JPEG);
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {

        }
    }

    @Override
    protected void onCancelled() {

    }

}


