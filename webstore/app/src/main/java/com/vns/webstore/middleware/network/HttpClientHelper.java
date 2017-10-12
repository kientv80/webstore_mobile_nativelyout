package com.vns.webstore.middleware.network;

import android.util.Pair;

import com.vns.webstore.middleware.entity.Profile;
import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.ProfileService;

import java.util.List;

/**
 * Created by LAP10572-local on 7/2/2016.
 */
public class HttpClientHelper {
    public static void executeHttpGetRequest(String url, HttpRequestListener callbackListener, String cachedName){
        new HttpGetRequest().execute(HttpClientHelper.buildRequestUrl(url), callbackListener, cachedName);
    }
    public static void executeHttpPostRequest(String url, HttpRequestListener callbackListener, List<Pair<String,String>> params){
        new HttpPostRequest().execute(HttpClientHelper.buildRequestUrl(url), callbackListener, params);
    }
    public static String buildRequestUrl(String url){
        String urlInfo = "";
        if(url.indexOf("?") < 0){
            urlInfo = "?version=" + AppConfigService.CLIENT_VERSION;
        }else{
            urlInfo = "&version=" + AppConfigService.CLIENT_VERSION;
        }
        Profile p = ProfileService.getProfile();
        if(p != null)
                urlInfo+="&uid=" + p.getId();

        return url + urlInfo;
    }
}
