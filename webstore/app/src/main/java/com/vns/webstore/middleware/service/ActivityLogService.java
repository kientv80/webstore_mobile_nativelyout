package com.vns.webstore.middleware.service;

import android.util.Pair;

import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.webstore.webstore.entity.UserActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by LAP10572-local on 10/19/2016.
 */
public class ActivityLogService {
    final BlockingQueue<UserActivity> activities = new ArrayBlockingQueue<UserActivity>(20);
    final List<UserActivity> logCache = new ArrayList<>();
    private static ActivityLogService instance;
    public static ActivityLogService getInstance(){
        if(instance == null)
            instance = new ActivityLogService();
        return instance;
    }
    public ActivityLogService(){
        userActivityLogConsumer();
    }
    public void logUserActivity(UserActivity activity){
        try {
            activities.put(activity);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void userActivityLogConsumer(){
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        UserActivity log = activities.take();
                        logCache.add(log);
                        if(logCache.size() > 3){
                            submitLogToServer();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void submitLogToServer() {
        if(logCache == null || logCache.isEmpty())
            return;

       StringBuilder data = new StringBuilder();
        for(UserActivity log : logCache){
            data.append(log.toString()).append("\n");
        }

        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<String, String>("data",data.toString()));
        HttpClientHelper.executeHttpPostRequest("http://360hay.com/log", new HttpRequestListener() {
            @Override
            public void onRecievedData(Object data, ErrorCode errorCode) {
                System.out.print(data);
            }
        }, params);
        logCache.clear();
    }
}
