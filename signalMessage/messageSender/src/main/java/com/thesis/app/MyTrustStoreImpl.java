package com.thesis.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.signalservice.api.push.TrustStore;

public class MyTrustStoreImpl implements TrustStore {

    @Override
    public InputStream getKeyStoreInputStream() {
        File initialFile = new File("/Users/evanchopra/Desktop/Thesis/signal-plugin/signalMessage/messageSender/src/main/java/com/thesis/app/newcert");
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

