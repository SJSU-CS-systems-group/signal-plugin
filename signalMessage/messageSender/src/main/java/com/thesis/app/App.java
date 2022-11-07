package com.thesis.app;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.whispersystems.signalservice.internal.configuration.SignalCdnUrl;
import org.whispersystems.signalservice.internal.configuration.SignalContactDiscoveryUrl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.state.SessionStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;
import org.whispersystems.libsignal.state.impl.InMemoryIdentityKeyStore;
import org.whispersystems.libsignal.state.impl.InMemoryPreKeyStore;
import org.whispersystems.libsignal.state.impl.InMemorySessionStore;
import org.whispersystems.libsignal.state.impl.InMemorySignedPreKeyStore;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.configuration.SignalServiceUrl;
import org.whispersystems.signalservice.internal.util.Util;

/**
 * Hello world!
 *
 */
public class App 
{
    myKeyStore keys;
    final String     URL         = "https://textsecure-service.whispersystems.org";
    final TrustStore TRUST_STORE = new MyTrustStoreImpl();
    final String     USERNAME    = "+16696968259";
    final String     PASSWORD    = "evanchopra1234";
    final String     USER_AGENT  = "computer";
    SignalServiceConfiguration x = new SignalServiceConfiguration( new SignalServiceUrl[]{new SignalServiceUrl(URL, TRUST_STORE)},
    new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)},
    new SignalContactDiscoveryUrl[]{new SignalContactDiscoveryUrl( "https://api.directory.signal.org", TRUST_STORE)});
    int registrationID;
    public static void main( String[] args ) throws InvalidKeyException, KeyStoreException
    {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keyStore = KeyStore.getInstance("BKS");

    }

    public void register(){
        keys = new myKeyStore();
        SignalServiceAccountManager accountManager = new SignalServiceAccountManager(x, null,
                                                                                    USERNAME, PASSWORD, USER_AGENT);
        try{
            String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODBaSHPYaibn9lWs18l1fipGpnMSr49MuhjmrnDv_Ej1Xr3ndVqDxuamQ4AYAopY-xQMrcDVIhGMVlbaT6Fs1bVpdtANChQJWsp7kGGGazM5PQhMOA3PP6TpaDXct_jq8ayheE28iRvLPMkDtIJHgBVAQdaJzd3rQ4_impUBORrjone_ICcYCDSYTAryNdDc7FqJD7NrTW7HCgiphBS-U352UNxcVCjezZc-3lNXn4Sp4exN55WKOSVmP-H3ZhVW_mQnTU9SiMmbxW1-uOr3IxspTu3XQLFkcw1n9a5qE_st1M9BS2qLgsSLRCiYRNGT-mapZ51A5wFlwPHbs15W1vA2DYTBCElBsbtjkP1wcwxJ0URiQEA7H2Uh5bK7rAAtfRWnUrUm4W00HVPCXbE71w3kI0BggRlN75zk64aIAwXYg67b5Tyn1wq3Y5GRkEpEVVxYvPQnry7Mc0J-26Txu6gsZphpwk8oPSo3V6bqoiPJLzjFQ--2lABZ_9nHTba9yZK1B5vdirqQhLhWHQzo8B65zG9ZWWkhbDHB7Axog0WAS2nhvBbV9-Dfg-9LfAiZMcOIs30V9t-OEX2QiQ818wWW4phRAPDUNJ7n48TpgtP13w2t0KAA5WiFOsOLiaQK29urNNKoEn9tUYgw7XvWfd6xa6kgJ4laEnV2Wu25yRhpg8tHGCHt9zlGa_Cj8VhwbcpNmbCY3oo851Hfy9bDi1Qrf0VXYLOZ3maxy98Hhkk1qSP2A9pa9Xl4xDAKkIu9ZYFea1hyeXguRzTMIzRpqJ10plo5WJDSJJT6WQxU0GYtx0AjJ3BS4kVgxVoZ4qFPiqQP9jPyYQRFdtEuztOeFxMZBtvYLfkmw3TllERxaUgN9HKIaxETZp2146awFZ4mehJnZ4RwvNNGODHCJCIoOh-W0na5nEqIzAwdIAbfD49WFAMOePM4TitjjMMJmO9DnkeMfoDIliP1yd75ravxvKRsQY5zmkGtWX_zr60pGB3bb1oGL9_ZOI4EJ9a12ylmKEOMyrSEANcc02hY-YMHkZjgDi5YbID3Da--jbHEb2pQO6QFdyV7dDiFMfqUA5jI7OoT4DGgzMKP1IZsljvjPSc6yWdVEDPRMrTOx-5pZ_V_8KwPHbhJrp3O0s-1WlhPrM8hbHOwtYpX2NWwR29H8PYWf6xTKSHYrQYtpOnB-A7ABGRiVPQWS4rP4DeBvF0UsEkJXp19DFJR6Zd4wXjKP-vNDF09fM9nB3HvIPqwddMTPRwHWbS2fyk3MVmjoIYnVF4XowRy";
            accountManager.requestSmsVerificationCode(false, Optional.fromNullable(signalCaptcha), Optional.fromNullable("x"));
        } catch (IOException ha) {
            ha.printStackTrace();
        }
        Scanner in = new Scanner(System.in);
 
        String code = in.nextLine();
        byte[] profileKey = Util.getSecretBytes(32);
        registrationID = KeyHelper.generateRegistrationId(false);
        byte[] unidentifiedAccessKey = UnidentifiedAccess.deriveAccessKeyFrom(profileKey);
        try {
            UUID id = accountManager.verifyAccountWithCode(code, null, registrationID, true, null, unidentifiedAccessKey, false);
            System.out.println(id);
        } catch (IOException rr) {
            rr.printStackTrace();
        }
        try {
            accountManager.setPreKeys(keys.getIdentityKeyPair().getPublicKey(), keys.getPreSignedKey(), keys.getPreKeysList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, int number, String name){
        SignalProtocolAddress receiptant =  new SignalProtocolAddress("evan", registrationID);
        SessionStore      sessionStore      = new InMemorySessionStore();
        PreKeyStore       preKeyStore       = new InMemoryPreKeyStore();
        SignedPreKeyStore signedPreKeyStore = new InMemorySignedPreKeyStore();
        IdentityKeyStore  identityStore     = new InMemoryIdentityKeyStore(keys.getIdentityKeyPair(), registrationID);
        SessionBuilder sessionBuilder = new SessionBuilder(sessionStore, preKeyStore, signedPreKeyStore,
                                                   identityStore, receiptant);
        //Build a new session from a org.whispersystems.libsignal.state.PreKeyBundle retrieved from a server.
        // But where do I get the prekey??
        sessionBuilder.process();
    }
}
