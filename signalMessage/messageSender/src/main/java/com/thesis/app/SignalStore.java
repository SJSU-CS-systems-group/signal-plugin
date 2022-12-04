package com.thesis.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.InvalidKeyIdException;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.groups.state.SenderKeyRecord;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SessionRecord;
import org.signal.libsignal.protocol.state.SignalProtocolStore;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

public class SignalStore implements SignalProtocolStore {

    IdentityKeyPair IKP;
    myKeyStore keyStore;
    int LocalRegistrationId;
    IdentityKey remoteIdentityKey;
    SignalProtocolAddress remoteServiceAddress;

    public SignalStore(myKeyStore x, int LocalRegistrationId){
        this.keyStore = x;
        this.IKP = x.identityKeyPair;
        this.LocalRegistrationId = LocalRegistrationId;

    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return this.IKP;
    }

    @Override
    public int getLocalRegistrationId() {
        return this.LocalRegistrationId;
    }

    @Override
    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        this.remoteServiceAddress = address;
        this.remoteIdentityKey = identityKey;
        return true;
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
        return false;
    }

    @Override
    public IdentityKey getIdentity(SignalProtocolAddress address) {
        return this.remoteIdentityKey;
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        return this.keyStore.getPreKeysList().get(0);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        this.keyStore.addPreKey(record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        if(this.keyStore.preKeys.containsKey(preKeyId) == true){
            return true;
        }
        return false;
    }

    @Override
    public void removePreKey(int preKeyId) {
        this.keyStore.preKeys.remove(preKeyId);       
    }

    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        return new SessionRecord();
    }

    @Override
    public List<SessionRecord> loadExistingSessions(List<SignalProtocolAddress> addresses) throws NoSessionException {
        return null;
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        return null;
    }

    @Override
    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
        
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        return false;
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        
    }

    @Override
    public void deleteAllSessions(String name) {
        
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        return this.keyStore.getPreSignedKey();
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        List<SignedPreKeyRecord> x = new ArrayList<SignedPreKeyRecord>();
        x.add(this.keyStore.getPreSignedKey());
        return x;
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        this.keyStore.signedPreKey.add(record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return false;
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        return;
    }

    @Override
    public void storeSenderKey(SignalProtocolAddress sender, UUID distributionId, SenderKeyRecord record) {
        this.keyStore.SenderKeysMap.put(distributionId, record);        
    }

    @Override
    public SenderKeyRecord loadSenderKey(SignalProtocolAddress sender, UUID distributionId) {
        // TODO Auto-generated method stub
        return this.keyStore.SenderKeysMap.get(distributionId);
    }

    
}
