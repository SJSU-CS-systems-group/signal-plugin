package com.thesis.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.signalservice.api.push.TrustStore;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MyTrustStoreImpl implements TrustStore {
    IdentityKeyPair identityKeyPair;
    int registrationId;
    List<PreKeyRecord> preKeys;
    SignedPreKeyRecord signedPreKey;

    public MyTrustStoreImpl () throws InvalidKeyException {
            identityKeyPair = KeyHelper.generateIdentityKeyPair();
            registrationId = KeyHelper.generateRegistrationId(false);
            preKeys = KeyHelper.generatePreKeys(1, 2);
            signedPreKey = KeyHelper.generateSignedPreKey(identityKeyPair, 10078957);
            try {
                MongoClient mongoClient = new MongoClient();
                DB database = mongoClient.getDB("db");
                DBCollection collection = database.getCollection("signalKeys");
                DBObject key = new BasicDBObject("_id", "signal")
                            .append("UUID", "")
                            .append("identityKeyPair", new BasicDBObject("publickey", identityKeyPair.getPublicKey())
                                .append("privatekey", identityKeyPair.getPrivateKey()))
                            .append("signedPreKey", new BasicDBObject("id", signedPreKey.getId())
                                .append("timestamp", signedPreKey.getTimestamp())
                                .append("publickey", signedPreKey.getKeyPair().getPublicKey())
                                .append("privatekey", signedPreKey.getKeyPair().getPrivateKey()))                             
                            .append("registrationID", registrationId);

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



    }

    @Override
    public InputStream getKeyStoreInputStream() {
        File initialFile = new File("messageSender/src/main/java/com/thesis/app/whisper.store");
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return targetStream;
    }

    @Override
    public String getKeyStorePassword() {
        return "whisper";
    }
}

