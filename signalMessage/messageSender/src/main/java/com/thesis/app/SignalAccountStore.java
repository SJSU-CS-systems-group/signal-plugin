package com.thesis.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.push.DistributionId;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

public class SignalAccountStore implements SignalServiceAccountDataStore {

    IdentityKeyPair IKP;
    myKeyStore keyStore;
    int LocalRegistrationId;
    IdentityKey remoteIdentityKey;
    SignalProtocolAddress remoteServiceAddress;

    public SignalAccountStore(myKeyStore x, int LocalRegistrationId){
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

    public SignalServiceAccountDataStore get(ServiceId accountIdentifier) {
        return null;
    }

    public SignalServiceAccountDataStore aci() {
        // TODO Auto-generated method stub
        return null;
    }

    public SignalServiceAccountDataStore pni() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void archiveSession(SignalProtocolAddress address) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<SignalProtocolAddress> getAllAddressesWithActiveSessions(List<String> addressNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<SignalProtocolAddress> getSenderKeySharedWith(DistributionId distributionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void markSenderKeySharedWith(DistributionId distributionId, Collection<SignalProtocolAddress> addresses) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearSenderKeySharedWith(Collection<SignalProtocolAddress> addresses) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isMultiDevice() {
        // TODO Auto-generated method stub
        return false;
    }

    
}
