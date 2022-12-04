package com.thesis.app;

import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.push.ServiceId;

public class SignalServiceDataStore{
    myKeyStore store;
    int regis;
    SignalServiceAccountDataStore pni;
    SignalServiceAccountDataStore aci;

    
    public SignalServiceDataStore(myKeyStore x, int regis){
        this.store = x;
        this.regis = regis;
        this.pni = (SignalServiceAccountDataStore) new SignalStore(this.store, this.regis);
        this.aci = (SignalServiceAccountDataStore) new SignalStore(this.store, this.regis);
    }
    public SignalServiceAccountDataStore get(ServiceId accountIdentifier) {
        return this.pni;
    }

    public SignalServiceAccountDataStore aci() {
        return this.aci;
    }

    public SignalServiceAccountDataStore pni() {
        // TODO Auto-generated method stub
        return this.pni;
    }

    public boolean isMultiDevice() {
        // TODO Auto-generated method stub
        return false;
    }
    
}