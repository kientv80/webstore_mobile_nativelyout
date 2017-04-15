package com.vns.webstore.middleware.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vns.webstore.middleware.entity.Websiteinfo;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LAP10572-local on 9/22/2016.
 */
public class AppConfigService {
    public static final String CLIENT_VERSION="2017.03.18.v1.0";

    public static Map<String,Websiteinfo> getWebsiteinfo() throws JSONException {
        Map<String,Websiteinfo> websiteinfoMap = new HashMap<>();
        String webinfo = LocalStorageHelper.getFromFile("webinfo");
        List<Websiteinfo> websiteinfoList = null;
        if(webinfo != null && !webinfo.isEmpty()){
            JSONObject jsonObject = new JSONObject(webinfo);
            if((System.currentTimeMillis() - jsonObject.getLong("cachedTime"))> 24*60*60*1000){
                HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/websiteinfo",null,"webinfo");
            }else{
                websiteinfoList = JSONHelper.toObjects(new JSONObject(jsonObject.getString("data")).getString("webinfo"), Websiteinfo.class);
            }
        }else{
            HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/websiteinfo",null,"webinfo");
        }
        if(websiteinfoList != null){
            for(Websiteinfo wi : websiteinfoList){
                websiteinfoMap.put(wi.getName(),wi);
            }
        }
        return websiteinfoMap;
    }

}
