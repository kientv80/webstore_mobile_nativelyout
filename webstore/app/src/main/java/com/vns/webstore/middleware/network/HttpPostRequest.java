package com.vns.webstore.middleware.network;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by LAP10572-local on 7/14/2016.
 */
public class HttpPostRequest extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        String urlString = params[0].toString();
        HttpRequestListener callback = (HttpRequestListener) params[1];
        List<Pair<String,String>> data = (List)params[2];
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        StringBuilder result = new StringBuilder();
        ErrorCode errorCode = new ErrorCode(ErrorCode.ERROR_CODE.SUCCESSED,"Successed");
        try {
            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
           // httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
           // httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            // Send request
            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(data));
            writer.flush();
            writer.close();
            os.close();

            int status = httpURLConnection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

        }catch (Exception ex){
            ex.printStackTrace();
            errorCode = new ErrorCode(ErrorCode.ERROR_CODE.EXCEPTION,"Failed");
        }finally {
            if(httpURLConnection != null)
                httpURLConnection.disconnect();

        }
        if(callback != null) {
            callback.onRecievedData(result.toString(), errorCode);
        }
        return result;
    }
    private static String getQuery(List<Pair<String,String>> params ) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String,String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }
}
