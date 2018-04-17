package com.vns.webstore.middleware.service;

import com.vns.webstore.middleware.entity.Websiteinfo;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LAP10572-local on 9/22/2016.
 */
public class AppConfigService {
    public static final String CLIENT_VERSION="1.0";
    public static final String DOMAIN="http://globalnewsindex.com";

    private static List<String> badImageWebsites = new ArrayList<>();
    public static boolean isGoodImageWebsite(String name){
        return !badImageWebsites.contains(name);
    }
    public static void checkVersion(HttpRequestListener listener){
        HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN + "/mobile/appversion",listener,null);
    }
    public static Map<String,Websiteinfo> getWebsiteinfo() throws JSONException {
        Map<String,Websiteinfo> websiteinfoMap = new HashMap<>();

        String webinfo = LocalStorageHelper.getFromFile("webinfo");
        List<Websiteinfo> websiteinfoList = null;
        if(webinfo != null && !webinfo.isEmpty()){
            JSONObject jsonObject = new JSONObject(webinfo);
            if((System.currentTimeMillis() - jsonObject.getLong("cachedTime"))> 24*60*60*1000){
                HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN +"/mobile/websiteinfo",null,"webinfo");
            }else{
                try {
                    websiteinfoList = JSONHelper.toObjects(new JSONObject(jsonObject.getString("data")).getString("webinfo"), Websiteinfo.class);
                }catch(Exception ex){
                    ex.printStackTrace();
                    LocalStorageHelper.saveToFile("webinfo","");
                    HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN +"/mobile/websiteinfo",null,"webinfo");
                }
                }
        }else{
            HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN +"/mobile/websiteinfo",null,"webinfo");
        }
        if(websiteinfoList != null && !websiteinfoList.isEmpty()){
            badImageWebsites.clear();
            for(Websiteinfo wi : websiteinfoList){
                websiteinfoMap.put(wi.getName(),wi);
                if(!wi.getIsGoodImage()){
                    badImageWebsites.add(wi.getName());
                }
            }
        }
        return websiteinfoMap;
    }

}
