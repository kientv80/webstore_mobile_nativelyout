package com.vns.webstore.middleware.service;

import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.storage.LocalStorageHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15/10/2017.
 */

public class SettingsService implements HttpRequestListener{
    private Map<String,Object> usersettings = new HashMap<>();
    private static SettingsService instance;
    public static SettingsService getInstance(){
        if(instance == null)
            instance = new SettingsService();
        return instance;
    }
    private SettingsService(){
        try {
            String data = LocalStorageHelper.getFromFile("settings");
            if(data!=null && !data.isEmpty()) {
                JSONObject settings = new JSONObject(data);
                JSONArray sts = new JSONObject(settings.getString("data")).getJSONArray("settings");
                if (sts != null && sts.length() > 0) {
                    for (int i = 0; i < sts.length(); i++) {
                        JSONObject item = sts.getJSONObject(i);
                        usersettings.put(item.getString("name"), item.get("value"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        loadSettings();
    }
    public void loadSettings(){
        HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN + "/mobile/settings/get?option=settings",this,"settings");
    }

    @Override
    public void onRecievedData(Object data, ErrorCode errorCode) {
        if (errorCode != null && errorCode.getErrorCode().equals(ErrorCode.ERROR_CODE.SUCCESSED)) {
            try {
                JSONObject settings = new JSONObject(data.toString());
                JSONArray sts = settings.getJSONArray("settings");
                if(sts != null && sts.length() >0){
                    for(int i=0;i<sts.length();i++){
                        JSONObject item = sts.getJSONObject(i);
                        usersettings.put(item.getString("name"),item.get("value"));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<String, Object> getSettings() {
        return usersettings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.usersettings = settings;
    }
}
