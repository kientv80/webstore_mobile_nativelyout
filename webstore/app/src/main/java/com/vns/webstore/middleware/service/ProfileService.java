package com.vns.webstore.middleware.service;
import android.content.Context;
import android.util.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vns.webstore.middleware.entity.DeviceInfo;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.utils.DeviceManager;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.middleware.entity.Profile;
import com.vns.webstore.middleware.storage.LocalStorageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAP10572-local on 8/29/2016.
 */
public class ProfileService {
    public static Profile p;
    public static Profile getProfile(Context context){
        if(p != null)
            return p;
        String profileJSon = LocalStorageHelper.getFromFile("profileinfo");
        if(profileJSon == null || profileJSon.isEmpty()){
            getProfileId(context);//load and next time, we should have it
        }else{
            JsonElement person =  new JsonParser().parse(profileJSon);
            p = new Profile(person.getAsJsonObject().get("id").getAsString(),"","","");
        }
        return p;
    }
    public static Profile getProfile(){
        if(p != null)
            return p;
        String profileJSon = LocalStorageHelper.getFromFile("profileinfo");
        if(profileJSon != null && !profileJSon.isEmpty()){
            JsonElement person =  new JsonParser().parse(profileJSon);
            p = new Profile(person.getAsJsonObject().get("id").getAsString(),"","","");
        }
        return p;
    }
    private static void getProfileId(Context context){
        DeviceInfo deviceInfo = DeviceManager.getIniqueIdForThisDevice(context);
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<String, String>("deviceid",deviceInfo.getUuid().toString()));
        params.add(new Pair<String, String>("deviceinfo",JSONHelper.toJSON(deviceInfo)));
        HttpClientHelper.executeHttpPostRequest("http://360hay.com/profile",new HttpRequestListener(){
            @Override
            public void onRecievedData(Object data, ErrorCode errorCode) {
                if(data != null){
                    LocalStorageHelper.saveToFile("profileinfo", data.toString());
                }
            }
        },params);

    }
}
