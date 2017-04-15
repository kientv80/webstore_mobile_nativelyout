package com.vns.webstore.middleware.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by LAP10572-local on 8/25/2016.
 */
public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }
    String file;
    public DownLoadImageTask(String file){
        this.file = file;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap logo = null;
        try{
            System.out.println(url);
            InputStream is = new URL(url).openStream();
            logo = BitmapFactory.decodeStream(is);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return logo;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(imageView != null)
            imageView.setImageBitmap(bitmap);
    }
}
