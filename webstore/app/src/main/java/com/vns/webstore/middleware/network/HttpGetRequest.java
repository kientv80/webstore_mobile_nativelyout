package com.vns.webstore.middleware.network;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.webstore.webstore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LAP10572-local on 7/14/2016.
 */
public class HttpGetRequest extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        String urlString = params[0].toString();
        HttpRequestListener callback = (HttpRequestListener) params[1];
        String cachedName = null;
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder result = new StringBuilder();
        if(params.length == 3 && params[2] != null)
            cachedName = params[2].toString();
        ErrorCode errorCode = null;

        if(ConnectionManager.isNetworkAvailable()) {
            try {
                url = new URL(urlString);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                int status = httpURLConnection.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                if(cachedName != null && result != null && !result.toString().isEmpty()){
                    JSONObject data = new JSONObject();
                    data.put("cachedTime",System.currentTimeMillis());
                    data.put("data",result.toString());
                    LocalStorageHelper.saveToFile(cachedName, data.toString());
                }
                errorCode = new ErrorCode(ErrorCode.ERROR_CODE.SUCCESSED, "");
            }catch (Exception ex){
                System.out.println("ERROR to access " + urlString);
                ex.printStackTrace();

                errorCode = new ErrorCode(ErrorCode.ERROR_CODE.CONNECTION_TIMEOUT, HttpClientHelper.context.getResources().getString(R.string.error_access_service));
            }finally {
                if(httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        }else{
            errorCode = new ErrorCode(ErrorCode.ERROR_CODE.NO_CONNECTION,HttpClientHelper.context.getResources().getString(R.string.error_noconnection));
        }

        if(callback != null) {
            callback.onRecievedData(result, errorCode);
        }
        return result;
    }
}
