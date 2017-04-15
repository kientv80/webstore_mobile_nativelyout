package com.vns.webstore.middleware.service;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vns.webstore.middleware.storage.LocalStorageHelper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by LAP10572-local on 8/18/2016.
 */
public class LocationService {
    public static final int MY_ACCESS_FINE_LOCATION = 12345;
    public static void requestLocationPermission(final Activity currentActivity, Context context){
        if (isLocationPermissionGrant(context)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                builder.setTitle("Yêu cầu vị trí")
                        .setMessage("Để thực hiện tìm quanh đây,ứng dụng cần biết vị trí của bạn.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_ACCESS_FINE_LOCATION);
                            }
                        });
                builder.create().show();
            }else {
                ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_ACCESS_FINE_LOCATION);
            }
        }
    }
    public static boolean isLocationPermissionGrant(Context context){
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
           return true;
        }
    }
    public static Location getLocation(final Activity currentActivity, Context context){
        if(!isLocationPermissionGrant(context))
            return null;

        LocationManager locationManager = (LocationManager) currentActivity.getSystemService(Activity.LOCATION_SERVICE);
        final long MIN_TIME_BW_UPDATES = 0;// 1000 * 60 * 1;
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;//10;
        Location location = null;
        LocationListener l = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationService.saveLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try{
            // First get location from Network Provider
            List<String> providers = locationManager.getAllProviders();
           for(String p : providers){
               locationManager.requestLocationUpdates(p, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,l);
               location = locationManager.getLastKnownLocation(p);
               if (location != null) {
                   break;
               }
           }
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
        return location;
    }
    public static void saveLocation(Location location) {
        if(location != null) {
            JSONObject locationJSon = new JSONObject();
            try {
                locationJSon.put("longitude", location.getLongitude() + "");
                locationJSon.put("latitude", location.getLatitude() + "");
                LocalStorageHelper.saveToFile("location", locationJSon.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
