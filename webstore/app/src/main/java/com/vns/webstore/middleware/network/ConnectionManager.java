package com.vns.webstore.middleware.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by LAP10572-local on 6/26/2016.
 */
public class ConnectionManager{

    public static void onReceive(Context context, Intent intent) {
        System.out.print(">>>>>>>>>>>>>>>" + intent.getAction());
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (isNetworkAvailable(context)) {
                connected = true;
            } else {
                connected = false;
            }
        }
    }
    private static Boolean connected = null;
    public static enum ConnectionType{WIFI,_3G, OTHER };

    public static boolean isNetworkAvailable() {
        if(connected != null)
            return connected;
        else
            return false;
    }
    public static boolean isNetworkAvailable(Context ctx) {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                connected = true;
            } else {
                connected = false;
            }
        return connected;
    }
    public static ConnectionType getConnectionType(Context ctx){
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                return ConnectionType.WIFI;
            }else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                return ConnectionType._3G;
            }
        }
        return ConnectionType.OTHER;
    }
}
