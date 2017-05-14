package com.webstore.webstore.entity;

import android.content.res.Configuration;

import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.ProfileService;

import java.text.SimpleDateFormat;

/**
 * Created by LAP10572-local on 10/19/2016.
 */
public class UserActivity {
    private String userId;
    private String action;
    private String type;
    private String data;
    private Long date;
    public UserActivity(String action,String type,String jsonData){
        try {
            setUserId(ProfileService.getProfile().getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        this.setAction(action);
        this.setType(type);
        this.setData(jsonData);
        this.setDate(System.currentTimeMillis());
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getDate()).append("\t").append(getUserId()).append("\t").append(AppConfigService.CLIENT_VERSION).append("\t").append(getType()).append("\t").append(getAction()).append("\t").append(getData());
        return b.toString();
    }
}
