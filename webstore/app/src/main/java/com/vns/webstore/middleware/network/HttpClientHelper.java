package com.vns.webstore.middleware.network;

import android.content.Context;
import android.util.Pair;

import com.vns.webstore.middleware.entity.Profile;
import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.ProfileService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by LAP10572-local on 7/2/2016.
 */
public class HttpClientHelper {
    public static Context context;

    public static void executeHttpGetRequest(String url, HttpRequestListener callbackListener, String cachedName){
        new HttpGetRequest().execute(HttpClientHelper.buildRequestUrl(url), callbackListener, cachedName);
    }
    public static void executeHttpPostRequest(String url, HttpRequestListener callbackListener, List<Pair<String,String>> params){
        new HttpPostRequest().execute(HttpClientHelper.buildRequestUrl(url), callbackListener, params);
    }
    public static String buildRequestUrl(String url){
        String urlInfo = "";
        if(url.indexOf("?") < 0){
            urlInfo = "?version=" + AppConfigService.CLIENT_VERSION ;
        }else{
            urlInfo = "&version=" + AppConfigService.CLIENT_VERSION ;
        }
        urlInfo= urlInfo+ "&worldnews=" + LocalStorageHelper.getFromFile("selectworldnews")+"&locale=" + Locale.getDefault();
        Profile p = ProfileService.getProfile();
        if(p != null)
                urlInfo+="&uid=" + p.getId();

        return url + urlInfo;
    }
}
