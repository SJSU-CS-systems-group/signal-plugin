package com.thesis.app;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.Random;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

public class myKeyStore {
    IdentityKeyPair identityKeyPair;
    int registrationId;
    List<PreKeyRecord> preKeys;
    SignedPreKeyRecord signedPreKey;

    public myKeyStore () throws InvalidKeyException, org.whispersystems.libsignal.InvalidKeyException {
        IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
        Random rand = new Random();
        int upperbound = 1000;
        int int_random = rand.nextInt(upperbound);
        List<PreKeyRecord> preKeyRecords = KeyHelper.generatePreKeys(int_random, 1000);
        signedPreKey = KeyHelper.generateSignedPreKey(identityKeyPair, preKeyRecords.get(0).getId());
    }

    public IdentityKeyPair getIdentityKeyPair(){
        return this.identityKeyPair;
    }

    public SignedPreKeyRecord getPreSignedKey() {
        return this.signedPreKey;
    }

    public List<PreKeyRecord> getPreKeysList() {
        return this.preKeys;
    }

}
