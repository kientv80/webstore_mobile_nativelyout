package com.vns.webstore.middleware.entity;

import java.util.UUID;

/**
 * Created by LAP10572-local on 7/13/2016.
 */
public class DeviceInfo {
    private String deviceId;
    private String simSerialNo;
    private String  deviceSoftwareVersion;
    private String networkOperatorName;
    private String simOperatorName;
    private String line1Number;
    private String subscriberId;
    private int networkType;
    private int phoneType;

    private UUID uuid;
    public DeviceInfo(){}
    public DeviceInfo(String deviceId,String simSerialNo){
        this.setDeviceId(deviceId);
        this.setSimSerialNo(simSerialNo);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSimSerialNo() {
        return simSerialNo;
    }

    public void setSimSerialNo(String simSerialNo) {
        this.simSerialNo = simSerialNo;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDeviceSoftwareVersion() {
        return deviceSoftwareVersion;
    }

    public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
        this.deviceSoftwareVersion = deviceSoftwareVersion;
    }

    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        this.networkOperatorName = networkOperatorName;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getLine1Number() {
        return line1Number;
    }

    public void setLine1Number(String line1Number) {
        this.line1Number = line1Number;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }
}
