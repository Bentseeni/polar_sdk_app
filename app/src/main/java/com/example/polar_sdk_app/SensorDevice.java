package com.example.polar_sdk_app;

public class SensorDevice
{

    String deviceId;
    String deviceAddress;
    String deviceRssi;
    String deviceName;
    Boolean deviceIsConnectable;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceRssi() {
        return deviceRssi;
    }

    public void setDeviceRssi(String deviceRssi) {
        this.deviceRssi = deviceRssi;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getDeviceIsConnectable() {
        return deviceIsConnectable;
    }

    public void setDeviceIsConnectable(Boolean deviceIsConnectable) {
        this.deviceIsConnectable = deviceIsConnectable;
    }


}
