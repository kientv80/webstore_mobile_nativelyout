package com.webstore.webstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vns.webstore.middleware.network.ConnectionManager;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.worker.WebstoreBackgroundService;

/**
 * Created by LAP10572-local on 11/4/2016.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
    static final String ACTION_WIFI_STATE_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(">>>>>>>>>>>>BootBroadcastReceiver>>>>>>>>>>>>>>>>"+intent.getAction());
        // BOOT_COMPLETED” start Service

        if (intent.getAction().equals(ACTION_USER_PRESENT)) {
            LocalStorageHelper.init(context);
            ConnectionManager.isNetworkAvailable(context);
            //Service
            if(!WebstoreBackgroundService.isRunning) {
                Intent serviceIntent = new Intent(context, WebstoreBackgroundService.class);
                context.startService(serviceIntent);
            }
        }else if (intent.getAction().equals(ACTION_WIFI_STATE_CHANGED)){
            LocalStorageHelper.init(context);
            ConnectionManager.isNetworkAvailable(context);
            //Service
            if(!WebstoreBackgroundService.isRunning) {
                Intent serviceIntent = new Intent(context, WebstoreBackgroundService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
