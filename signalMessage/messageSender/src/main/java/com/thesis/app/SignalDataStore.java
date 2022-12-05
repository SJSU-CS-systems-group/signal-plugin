package com.thesis.app;

import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.push.ServiceId;

public class SignalDataStore implements SignalServiceDataStore{
    myKeyStore store;
    int regis;
    SignalServiceAccountDataStore pni;
    SignalServiceAccountDataStore aci;

    
    public SignalDataStore(myKeyStore x, int regis){
        this.store = x;
        this.regis = regis;
        this.pni = (SignalServiceAccountDataStore) new SignalAccountStore(this.store, this.regis);
        this.aci = (SignalServiceAccountDataStore) new SignalAccountStore(this.store, this.regis);
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