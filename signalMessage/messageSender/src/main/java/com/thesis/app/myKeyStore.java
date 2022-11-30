package com.thesis.app;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.signal.libsignal.protocol.util.KeyHelper;
import org.signal.libsignal.zkgroup.InvalidInputException;
import org.signal.libsignal.zkgroup.profiles.ProfileKey;
import org.whispersystems.signalservice.internal.push.PreKeyEntity;
import org.whispersystems.signalservice.internal.util.Util;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.ecc.Curve;
import org.signal.libsignal.protocol.ecc.ECKeyPair;

public class myKeyStore {
    IdentityKeyPair identityKeyPair;
    int registrationId;
    List<PreKeyRecord> preKeys;
    SignedPreKeyRecord signedPreKey;

    public myKeyStore () throws InvalidKeyException, org.whispersystems.libsignal.InvalidKeyException {
        this.identityKeyPair = generateIdentityKey();
        Random rand = new Random();
        int upperbound = 1000;
        int int_random = rand.nextInt(upperbound);
        this.preKeys = new ArrayList<PreKeyRecord>();
        this.preKeys.add(new PreKeyRecord(100, generateECKeyPair()));
        this.signedPreKey = new SignedPreKeyRecord(int_random, int_random, generateECKeyPair(), new byte[8]);
    }


    // public myKeyStore(IdentityKeyPair key) throws org.whispersystems.libsignal.InvalidKeyException{
    //     this.identityKeyPair = key;
    //     Random rand = new Random();
    //     int upperbound = 1000;
    //     int int_random = rand.nextInt(upperbound);
    //     this.preKeys = KeyHelper.generatePreKeys(int_random, 1000);
    //     this.signedPreKey = KeyHelper.generateSignedPreKey(this.identityKeyPair, this.preKeys.get(0).getId());
    // }

    public ECKeyPair generateECKeyPair(){
        return Curve.generateKeyPair();
    }

    public IdentityKeyPair getIdentityKeyPair(){
        return this.identityKeyPair;
    }

    private static IdentityKeyPair generateIdentityKey() {
        ECKeyPair djbKeyPair = Curve.generateKeyPair();
        return new IdentityKeyPair(new IdentityKey(djbKeyPair.getPublicKey()),djbKeyPair.getPrivateKey());
      }

    public SignedPreKeyRecord getPreSignedKey() {
        return this.signedPreKey;
    }

    public List<PreKeyRecord> getPreKeysList() {
        return this.preKeys;
    }

    public static ProfileKey generateProfileKey() throws InvalidInputException {
        return new ProfileKey(Util.getSecretBytes(32));
     }

}
