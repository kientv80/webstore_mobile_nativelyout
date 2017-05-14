package com.vns.webstore.middleware.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.vns.webstore.middleware.entity.DeviceInfo;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by LAP10572-local on 7/13/2016.
 */
public class DeviceManager {
    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected volatile static DeviceInfo deviceInfo;

    public static DeviceInfo getIniqueIdForThisDevice(Context context) {
        if (deviceInfo == null) {
            synchronized (DeviceManager.class) {
                UUID uuid = null;
                TelephonyManager tm = null;
                try {
                    int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    } else {
                        //Ask permission
                    }
                    if (deviceInfo == null) {
                        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                        final String id = prefs.getString(PREFS_DEVICE_ID, null);
                        if (id != null) {
                            // Use the ids previously computed and stored in the
                            // prefs file
                            uuid = UUID.fromString(id);
                        } else {
                            final String androidId = Settings.Secure.getString(
                                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            // Use the Android ID unless it's broken, in which case
                            // fallback on deviceId,
                            // unless it's not available, then fallback on a random
                            // number which we store to a prefs file
                            try {
                                if (!"9774d56d682e549c".equals(androidId)) {
                                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                                } else {
                                    final String deviceId = (tm.getDeviceId());
                                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            // Write the value out to the prefs file
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                        }
                    }
                } catch (Exception ex) {
                }
                if (uuid == null) {
                    uuid = UUID.randomUUID();
                }
                if (tm != null) {
                    deviceInfo = new DeviceInfo(tm.getDeviceId(), tm.getSimSerialNumber());
                    deviceInfo.setDeviceSoftwareVersion(tm.getDeviceSoftwareVersion());
                    deviceInfo.setNetworkOperatorName(tm.getNetworkOperatorName());
                    deviceInfo.setSimOperatorName(tm.getSimOperatorName());
                    deviceInfo.setLine1Number(tm.getLine1Number());
                    deviceInfo.setSubscriberId(tm.getSubscriberId());
                    deviceInfo.setNetworkType(tm.getNetworkType());
                    deviceInfo.setPhoneType(tm.getPhoneType());
                } else {
                    deviceInfo = new DeviceInfo();
                }
                deviceInfo.setUuid(uuid);

            }
        }
        return deviceInfo;
    }

    public static DeviceInfo getDeviceInfo(Context context) {
        String deviceId, simSerialNo;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // device Id
        deviceId = telephonyManager.getDeviceId();
        // serial number
        simSerialNo = telephonyManager.getSimSerialNumber();
        DeviceInfo dv = new DeviceInfo(deviceId, simSerialNo);
        return dv;

    }
}
