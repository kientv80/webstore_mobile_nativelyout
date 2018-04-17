package com.vns.webstore.middleware.service;

import com.vns.webstore.middleware.entity.NotifyInfo;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.middleware.entity.NotifyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 19/03/2017.
 */

public class WebstoreService {

    public static final String NOTIFICATIONS = "notifications";

    private static List<NotifyInfo> oldNotifyInfoList = null;

    public static void checkNewNotification(final NotifyListener listener) {
        try {
            String oldNotify = LocalStorageHelper.getFromFile(NOTIFICATIONS);
            String cachedTime = null;
            if (oldNotify != null) {
                try {
                    JSONObject oldNotifications = new JSONObject(oldNotify);
                    if (oldNotifications.has("data") && oldNotifications.getString("data") != null)
                        oldNotifyInfoList = JSONHelper.toObjects(oldNotifications.getString("data"), NotifyInfo.class);
                    cachedTime = oldNotifications.getString("cachedTime");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(cachedTime == null)
                cachedTime = (System.currentTimeMillis()) + "";
            final JSONObject cached = new JSONObject();
            cached.put("cachedTime", cachedTime);
            HttpClientHelper.executeHttpGetRequest(AppConfigService.DOMAIN + "/getupdate?lasttimeupdate=" + cachedTime, new HttpRequestListener() {
                @Override
                public void onRecievedData(Object data, ErrorCode errorCode) {
                    if (data != null) {
                        //Save in file and it will be use at a suitable time
                        String newNotify = data.toString();
                        try {

                            List<NotifyInfo> newNotifyInfoList = JSONHelper.toObjects(newNotify, NotifyInfo.class);
                            int newNotifyCount = newNotifyInfoList.size();
                            if (newNotifyCount > 0) {
                                if (oldNotifyInfoList != null) {
                                    for (NotifyInfo n : oldNotifyInfoList) {
                                        if (!newNotifyInfoList.contains(n))
                                            newNotifyInfoList.add(n);
                                    }
                                }

                                if (newNotifyInfoList.size() > 20) {
                                    List<NotifyInfo> updateNotifyInfoList = newNotifyInfoList.subList(0, 20);
                                    cached.put("data", JSONHelper.toJSON(updateNotifyInfoList));
                                } else {
                                    cached.put("data", JSONHelper.toJSON(newNotifyInfoList));
                                }
                                LocalStorageHelper.saveToFile(NOTIFICATIONS, cached.toString());
                                listener.haveNewUpdate(newNotifyInfoList, newNotifyCount);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
