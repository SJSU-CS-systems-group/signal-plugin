package com.thesis.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Timestamp;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.whispersystems.signalservice.internal.ServiceResponse;
import org.whispersystems.signalservice.internal.configuration.SignalCdnUrl;
import org.whispersystems.signalservice.internal.configuration.SignalCdsiUrl;
import org.whispersystems.signalservice.internal.configuration.SignalContactDiscoveryUrl;
import org.whispersystems.signalservice.internal.configuration.SignalKeyBackupServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalProxy;
import org.signal.libsignal.metadata.certificate.InvalidCertificateException;
import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.signal.libsignal.zkgroup.InvalidInputException;
import org.signal.libsignal.zkgroup.profiles.ClientZkProfileOperations;
import org.signal.libsignal.zkgroup.profiles.ProfileKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.state.SessionStore;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;
import org.whispersystems.libsignal.state.impl.InMemoryIdentityKeyStore;
import org.whispersystems.libsignal.state.impl.InMemoryPreKeyStore;
import org.whispersystems.libsignal.state.impl.InMemorySessionStore;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;
import org.whispersystems.libsignal.state.impl.InMemorySignedPreKeyStore;
import org.signal.libsignal.protocol.util.KeyHelper;
import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.account.AccountAttributes;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.ServiceIdType;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.api.storage.SignalAccountRecord;
import org.whispersystems.signalservice.api.websocket.HealthMonitor;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.configuration.SignalServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalStorageUrl;
import org.whispersystems.signalservice.internal.push.RequestVerificationCodeResponse;
import org.whispersystems.signalservice.internal.push.VerifyAccountResponse;
import org.whispersystems.signalservice.internal.util.DynamicCredentialsProvider;
import org.whispersystems.signalservice.internal.util.Util;
import org.whispersystems.signalservice.internal.websocket.WebSocketConnection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.WebSocket;

/**
 * Hello world!
 *
 */
public class App {
    myKeyStore keys;
    final String URL;
    final TrustStore TRUST_STORE;
    final String USERNAME;
    final String PASSWORD;
    final String USER_AGENT;
    SignalProtocolStore proto;
    SignalServiceUrl[] allUrls;
    SignalServiceConfiguration conf;
    Preferences prefs;
    int registrationID;
    SignalServiceAccountManager accountManager;
    int pniRegistrationId;
    ACI aci;
    PNI pni;
    byte[] unidentifiedAccessKey; 

    public App() {
        this.URL = "https://textsecure-service.whispersystems.org";
        this.TRUST_STORE = new MyTrustStoreImpl();
        this.USERNAME = "+14085026204";
        this.PASSWORD = "evanchopra1234";
        this.USER_AGENT = "Mozilla/5.0 (Linux; Android 12; Pixel 6 Build/SD1A.210817.023; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/94.0.4606.71 Mobile Safari/537.36";
        this.allUrls = new SignalServiceUrl[] { new SignalServiceUrl(this.URL, this.TRUST_STORE) };
        Map<Integer, SignalCdnUrl[]> signalCDNMap = new HashMap<Integer, SignalCdnUrl[]>();
        signalCDNMap.put(0, new SignalCdnUrl[] { new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE) });
        signalCDNMap.put(2, new SignalCdnUrl[] { new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE) });
        SignalKeyBackupServiceUrl[] SKBS = {};
        SignalStorageUrl[] SSU = {};
        SignalCdsiUrl[] SCU = {};
        List<Interceptor> Int_ceptor = new ArrayList<Interceptor>();
        Optional<Dns> dns = Optional.empty();
        Optional<SignalProxy> proxy = Optional.empty();
        this.conf = new SignalServiceConfiguration(allUrls, signalCDNMap,
                new SignalContactDiscoveryUrl[] {
                        new SignalContactDiscoveryUrl("https://api.directory.signal.org", TRUST_STORE) },
                SKBS, SSU, SCU, Int_ceptor, dns, proxy, null);
        // this.conf = new SignalServiceConfiguration( new SignalServiceUrl[]{new
        // SignalServiceUrl(URL, TRUST_STORE)},
        // new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)},
        // new SignalContactDiscoveryUrl[]{new SignalContactDiscoveryUrl(
        // "https://api.directory.signal.org", TRUST_STORE)});
        this.prefs = Preferences.userNodeForPackage(App.class).node(App.class.getSimpleName());
    }

    public static void main(String[] args)
            throws InvalidKeyException, KeyStoreException, BackingStoreException, IOException, InterruptedException {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keyStore = KeyStore.getInstance("BKS");
        App example = new App();
        example.register();
        example.sendNewPreKeyBundle();
        example.sendMessage();

    }

    public void saveNeedKeys() {

    }

    public void register() throws BackingStoreException, UnsupportedEncodingException {
        prefs.clear();
        try {
            this.keys = new myKeyStore();
        } catch (java.security.InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        }
        // String str = new String(this.keys.identityKeyPair.serialize());
        // prefs.put("OG_IdentityKey", str);
        this.accountManager = new SignalServiceAccountManager(this.conf,
                new DynamicCredentialsProvider(null, null, this.USERNAME, this.PASSWORD,
                        SignalServiceAddress.DEFAULT_DEVICE_ID),
                this.USER_AGENT,
                null,
                true);
        String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODDXeuB0GQZFRJ-BFNy9vnxlsYFcuqohAO-8UidiHu_4XbgLW070XJjlggY3If61gVLeI5CgX8DwylbQFWq6jIaN4mR-X1LjQn9Eegmf5SLnDdk7vZSVpqQR0bMuffTRpsaCVbfODr4w8GuoT2NtTGqz0ZD6QSM6eI2EyksoQ1BIuSUhPvIDwzVEWXgXijrcbgzrjEgAFlaXb7j-uZCrmLLbYxYLvIIzpyndmmRyR1fYpmlQK9TFqIX3Uggqap6sPVJfY9l8Zk5WLhJUafd7nKRbWQ3o6cZETIdsUdv9HiQrXUVYysAI-uU-0E15k7Aih3Lg9wkc0xiDQtrQyXlGIBayo5m8TP69KPAnW0seDUxXW-VVKOEXGiATMqQVDBZHx6dwyme0EqOhaUrShJ8kjFtaJyeoOW3gQSaM4xCSRt7jx6UD4XK8RXoCR9SkOAc9mHG23dEMEQcEmgHnc15Nhtyju0EJzBesUqjRQWs8Jrmz0QCyl4oNMrxIUgBeUf3AZlsNV7poC7HJQBt--x1iH4uwwRYueEZH1Ug1JPyEisd8Uet_F8EFTV58xJPxr_Qx1KjGoQ9GlWRCZDXKfwJ1ngJKL1OYzkCeKqgrCP0Skcl6bVKs5E_ByUn48UU6BxPAmRJuKQqXG2U0o1CHK4nXmo5budIinlg-EtK8t19ZEraKk55RLvbNVykOsDI3_dazeefBf0GFjK8Y0lCwvXp_e-rc6-RxnHc9Q8zcwKYesimqz946997mg8DNQ6Pun205L7J4l2FRzUZlsx8NT4MipUGcNDMoWJkchhRjKt3vzvgszWDcJJfGFcFMCfbe4KYpT4iGZUr3IcLQdYn5VV8K3OcbMosBuNrnCLWpGVTHJ7U9OgRJ0MPn2aod61tKDhKSOm1rswejT7b4wDTZZOVP4JHakm4L01eWASvdEMshk2Fg3PUUJGjZPMbFquGCZaTNe7-5R8SyVyttehpNWFFXGM-YOigKLgvxd-lmOOcrtYv7Yu7MWJbcDi_k4Xaz3Y9xWm19B0lc7SJ9otc6yTOTn0sWWZvA4sr29Cld0DcpC723t7T7urPmoqu3FnfGCvTWrd-KX_RrO_f10V8ue9tgXU2FAVgqpSZQyef81C8hugd9dPPj3puKTBoZphE9-dOUkPDK3p-SAExUcvtF41ywTpKj4POO1s2XyAAuhm3qcIuffG-jDygvRHjNXcFiv_bMd_bxzvQZHiY15ZZlU2a3e_MAGbTE_hhQyuDsOHKAUfG97mmq-oBIDlV0VRVEaWMuJ6TfouHn6sEw7EmYxeIx0mWFwaYOgnc-btpBAseJUupF_5ID_z9w1lT6eRpjciwpfZCyNJcU1D9_VrB12rsX0DiO6x-uvQ";
        ServiceResponse<RequestVerificationCodeResponse> hi = this.accountManager.requestSmsVerificationCode(false,
                Optional.ofNullable(signalCaptcha), Optional.ofNullable("x"), Optional.ofNullable("x"));
        System.out.println(hi.getStatus());
        Scanner in = new Scanner(System.in);

        String code = in.nextLine();
        ProfileKey profileKey = null;
        try {
            profileKey = myKeyStore.generateProfileKey();
        } catch (InvalidInputException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.registrationID = KeyHelper.generateRegistrationId(false);
        this.pniRegistrationId = KeyHelper.generateRegistrationId(false);
        // prefs.put("REGISTRATION_ID", Integer.toString(this.registrationID));
        this.unidentifiedAccessKey = UnidentifiedAccess.deriveAccessKeyFrom(profileKey);
        int signalKey = 1324;
        // UUID id = null;
        ServiceResponse<VerifyAccountResponse> example = null;
        example = this.accountManager.verifyAccount(code, signalKey, true, unidentifiedAccessKey, true,
                new AccountAttributes.Capabilities(false,
                        true,
                        false,
                        true,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false),
                true, this.pniRegistrationId);
        System.out.println(example.getResult());
        Optional<VerifyAccountResponse> result = example.getResult();
        this.aci = ACI.parseOrNull(result.get().getUuid());
        this.pni = PNI.parseOrNull(result.get().getPni());

        this.accountManager = new SignalServiceAccountManager(this.conf,
                new DynamicCredentialsProvider(aci, pni, this.USERNAME, this.PASSWORD,
                        SignalServiceAddress.DEFAULT_DEVICE_ID),
                this.USER_AGENT,
                null,
                true);

        try {
            this.accountManager.setAccountAttributes(null, this.registrationID, true, null, null, unidentifiedAccessKey,
                    true,
                    new AccountAttributes.Capabilities(false,
                            true,
                            false,
                            true,
                            true,
                            true,
                            true,
                            true,
                            false,
                            false),
                    true, null, this.pniRegistrationId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // prefs.put("UUID", id.toString());
        // prefs.put("SIGNALING_KEY", Integer.toString(signalKey));
        System.out.println(example.getResult().get().toString());
        System.out.println(example.getResult().get().getUuid());
    }

    public void sendNewPreKeyBundle() throws IOException, InvalidKeyException {
        this.accountManager.setPreKeys(ServiceIdType.PNI, this.keys.getIdentityKeyPair().getPublicKey(),
                this.keys.getPreSignedKey(), this.keys.getPreKeysList());
        this.accountManager.setPreKeys(ServiceIdType.ACI, this.keys.getIdentityKeyPair().getPublicKey(),
                this.keys.getPreSignedKey(), this.keys.getPreKeysList());
    }

    public void sendMessage(){
        CredentialsProvider creds = new CredentialsProvider(this.aci, this.pni, this.USERNAME, this.PASSWORD, this.pniRegistrationId);
        SignalServiceDataStore dataStore = (SignalServiceDataStore) new SignalStore(this.keys, this.pniRegistrationId);
        WebSocketFactory fact = new WebSocketFactory() {
            @Override
            public WebSocketConnection createWebSocket() {
                WebSocketConnection x = new WebSocketConnection("evan", conf, null, "message", null, "", false);
                return x;
            }

            @Override
            public WebSocketConnection createUnidentifiedWebSocket() {
                WebSocketConnection x = new WebSocketConnection("evan", conf, null, "message", null, "", false);
                return x;
            }
            
        };
        SignalWebSocket web = new SignalWebSocket(fact);
        Long envSize = (long) 1000;
        SignalServiceMessageSender sender = new SignalServiceMessageSender(conf, (org.whispersystems.signalservice.api.util.CredentialsProvider) creds, dataStore, null, "message", web, null, null, null, envSize, false);
        long timestamp = System.currentTimeMillis();
        SignalServiceDataMessage.Builder messageBuilder = SignalServiceDataMessage.newBuilder();
        messageBuilder.withTimestamp(timestamp);
        messageBuilder.withBody("hello");
        SignalServiceDataMessage finalMessage = messageBuilder.build();
        SignalServiceAddress toAddress = new SignalServiceAddress(null, "+14088075656");
        UnidentifiedAccess firstKey = null;
        try {
            firstKey = new UnidentifiedAccess(this.unidentifiedAccessKey, this.unidentifiedAccessKey);
        } catch (InvalidCertificateException e1) {
            e1.printStackTrace();
        }
        UnidentifiedAccessPair pairs = new UnidentifiedAccessPair(firstKey, firstKey);
        try {
            sender.sendDataMessage(toAddress, Optional.ofNullable(pairs), ContentHint.DEFAULT, finalMessage, null, false, false);
        } catch (UntrustedIdentityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }      

}