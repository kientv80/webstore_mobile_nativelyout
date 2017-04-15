package com.vns.webstore.openapi;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.vns.webstore.middleware.service.ProfileService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;

/**
 * Created by LAP10572-local on 6/14/2016.
 */
public class CoreOpenAPIs {
    Context mContext;
    public CoreOpenAPIs(Context context){
        mContext = context;
    }
    @JavascriptInterface
    public String getUserId(){
        return ProfileService.getProfile(mContext).getId();
    }
    @JavascriptInterface
    public String getAccessToken(){
        return "";
    }

    @JavascriptInterface
    public String getLocation(){
        String location = LocalStorageHelper.getFromFile("location");
        if(location == null || location.isEmpty()) {
            Toast.makeText(mContext, "Cannot load location, please click on the menu and grant access location to use this function", Toast.LENGTH_LONG).show();
            return null;
        }
        return location;
    }
}
