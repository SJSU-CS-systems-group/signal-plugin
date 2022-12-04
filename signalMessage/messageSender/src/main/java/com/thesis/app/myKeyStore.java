package com.thesis.app;

import java.security.InvalidKeyException;
import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
import org.signal.libsignal.protocol.groups.state.SenderKeyRecord;


public class myKeyStore {
    IdentityKeyPair identityKeyPair;
    int registrationId;
    HashMap<Integer, PreKeyRecord> preKeys;
    List<SignedPreKeyRecord> signedPreKey = new ArrayList<SignedPreKeyRecord>();
    HashMap<UUID,SenderKeyRecord> SenderKeysMap;
    int batch;

    public myKeyStore () throws InvalidKeyException, org.whispersystems.libsignal.InvalidKeyException {
        this.identityKeyPair = generateIdentityKey();
        Random rand = new Random();
        int upperbound = 1000;
        int int_random = rand.nextInt(upperbound);
        this.preKeys = generatePreKeyRecords(1, 100);
        this.signedPreKey.add(generateSignedPreKeyRecord(int_random));
    }

    // From signal-cli
    public HashMap<Integer,PreKeyRecord> generatePreKeyRecords(int offset, final int batchSize) {
        this.batch = batchSize;
        HashMap<Integer,PreKeyRecord> x = new HashMap<Integer,PreKeyRecord>();
        for (int i = 0; i < batchSize; i++) {
            int preKeyId = (offset + i) % 10000000;
            ECKeyPair keyPair = generateECKeyPair();
            PreKeyRecord record = new PreKeyRecord(preKeyId, keyPair);

            x.put(preKeyId, record);
        }
        return x;
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
        return this.signedPreKey.get(0);
    }

    public List<PreKeyRecord> getPreKeysList() {
        List<PreKeyRecord> list = new ArrayList<PreKeyRecord>(this.preKeys.values());
        return list;
    }

    public void addPreKey(PreKeyRecord x){
        this.batch += 1;
        this.preKeys.put(this.batch,x);
    }

    public static ProfileKey generateProfileKey() throws InvalidInputException {
        return new ProfileKey(Util.getSecretBytes(32));
     }

}
