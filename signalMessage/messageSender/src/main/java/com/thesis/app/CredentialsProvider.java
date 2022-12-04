package com.thesis.app;

import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;

public class CredentialsProvider {
    ACI aci;
    PNI pni;
    String number;
    String password;
    int deviceId;
    public CredentialsProvider(ACI aci, PNI pni, String number, String password, int deviceId){
        this.aci = aci;
        this.pni = pni;
        this.number = number;
        this.password = password;
        this.deviceId = deviceId;
        
    }
    public ACI getAci() {
        return this.aci;
    }
    public PNI getPni() {
        return this.pni;
    }
    public String getE164() {
        return this.number;
    }
    public String getPassword() {
        return this.password;
    }
    public int getDeviceId() {
        return this.deviceId;
    }
}

