package com.thesis.app;

import java.security.InvalidKeyException;
import java.security.interfaces.ECKey;
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
        this.preKeys = generatePreKeyRecords(1, 100);
        this.signedPreKey = generateSignedPreKeyRecord(int_random);
    }

    // From signal-cli
    public List<PreKeyRecord> generatePreKeyRecords(int offset, final int batchSize) {
        ArrayList<PreKeyRecord> records = new ArrayList<PreKeyRecord>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            int preKeyId = (offset + i) % 10000000;
            ECKeyPair keyPair = generateECKeyPair();
            PreKeyRecord record = new PreKeyRecord(preKeyId, keyPair);

            records.add(record);
        }
        return records;
    }

    public SignedPreKeyRecord generateSignedPreKeyRecord(int random) {
        ECKeyPair keyPair = generateECKeyPair();
        byte[] signature = null;
        try {
            signature = Curve.calculateSignature(this.identityKeyPair.getPrivateKey(), keyPair.getPublicKey().serialize());
        } catch (org.signal.libsignal.protocol.InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new SignedPreKeyRecord(random, System.currentTimeMillis(), keyPair, signature);
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
