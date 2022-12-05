package com.thesis.app;

import java.io.IOException;
import java.io.Serial;
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
import org.whispersystems.signalservice.api.messages.SignalServiceContent;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.ServiceIdType;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.api.storage.SignalAccountRecord;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
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
        Thread.sleep(30000);
        // try {
        //     example.sendMessage();
        // } catch (InvalidInputException e) {
        //     e.printStackTrace();
        // } catch (InvalidCertificateException e) {
        //     e.printStackTrace();
        // }
        example.recieveMessage();

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
        String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODDUzZA7j_CdbZ3dFy-LWQ8m6XFXHM3JO7F0tT5WSQe_YoFNgTl3pA3HP4dAcpIN7P_NRKjqgHdN5dKuICVyRMXYu3RYn7M8bL3N8czUh6fNwapbzcWLQ3q4jaT7XjKHrXrtBC54Qs4CMBMRaM6bzLUxu2zwb_mF5V75g58VKf6HZBm_wZjUn5Zru8yf5jES0O3TV_Ytq3PKu47j8cL9tu2UFs__Lw1nbXvXoMDGDvhhfpIxLkujbpLU_fA789Brs8vvHz6X-XA27q_E1RWrXNdytwXWK5KDPRwt7reLZkQWizC9ZpmqL3kkFhSEOibnZQXpFuDKbQvHpOh6xThO4vt2tLyVFDktMfBbeUdgLIs71fG-uuqPNIU_6zwrUSevfa9n6rO0pDTLYyTZDPDt5fOOACuifxWjnyA3D8LsLC2jORyrdjWTlCerIn2SPpWZHUZ4B57tdeNVItxVawYztFQfbV66TJjNJ8_ERKoVx2khCfkw71DJ4ridVMWFI9jq8uIOn4xfLpK4f6uh3Fx6yE8obcFmrdi2HU0TwezMrWrmld19ub-V2Y4LPSEI2RqYIsFDiIeAvLCp3h6ArCn6zTLlEtzNmYSzTegF-WAZY0IYL8yKyCyKh6AWsb1Wg2UjvJrkrX3t7AFS-1k_Lk80dde_RHeqpw-uMSf4_WfZdQa6CkUmcfKE4gxDiZxuOZ2aVid8Xg5WU7Pl1eRCGwhQFhyiR_JFPI_Epi0BbpXTzXd3Rp36IgXOHoyJneM8CZPw51LIxn5ZjjeVeNkha9Z6vWQeIgCEL7cH9L-L8ZOTCP37xkJjrlR7fmoMSijzMv06Ye7VAjAk6uGVxUu6-AWnt1s_tQEAflihlkb6qu-yZVRjT4vxNhEXcki6E0Gb50GjsO8ZiU_boOYV3WLvv3ojaexFl5GbDdbQh0GMGFGGjvM5Tip3JybeeU2sjx1TIq8qieBRXg2yW156we7fBk1KH1zcq9YBMk6zmK_-vS89dk90GCEHPV8pj-e0bqHIVcVZA-QiXw_S-ylPrRsYJ0QFW6CmnCq4uoNcdZtpkds68ZSQGBheOWL0_74ikrRgIq9SCE-Xj0f7vLF6oykUcd5eYuo9WusWTmBJikAvn5PYnzg8hJpSbO1xi1RDZ4vyWWu7dw2CdZA8klB_GCG14mSrr9kugV8s8FYWSgGksyBiN38u6Ymyb_p4hWDN1QmVnL3xjsx_S4fM816Zjm14iee1F4by0y7dpo2berW2kyhPDT4-LRUhtAbTb9EIh3TnpvlWyu6NPddUHIFN1GpN3goFp6k0bLT8HA83sPKHxjUE3Uufi0ceOOH65UTCOkg0VoWEokHB7r5l8DAB-DQByNGOMHIOxU9yyw";
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

    public void sendMessage() throws InvalidInputException, InvalidCertificateException{
        CredentialsProvider creds = new credsProd(this.aci, this.pni, this.USERNAME, this.PASSWORD, this.pniRegistrationId);
        SignalServiceDataStore dataStore = new SignalDataStore(this.keys, this.pniRegistrationId);
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
        SignalServiceMessageSender sender = new SignalServiceMessageSender(conf, creds, dataStore, null, "message", web, null, null, null, envSize, false);
        long timestamp = System.currentTimeMillis();
        SignalServiceDataMessage.Builder messageBuilder = SignalServiceDataMessage.newBuilder();
        messageBuilder.withTimestamp(timestamp);
        messageBuilder.withBody("hello");
        SignalServiceDataMessage finalMessage = messageBuilder.build();
        SignalServiceAddress toAddress = new SignalServiceAddress(this.pni, "+14088075656");
        byte[] senderCert = UnidentifiedAccess.deriveAccessKeyFrom(myKeyStore.generateProfileKey());
        UnidentifiedAccess self = new UnidentifiedAccess(this.unidentifiedAccessKey, senderCert);
        UnidentifiedAccessPair pairs = new UnidentifiedAccessPair(null, self);
        try {
            sender.sendDataMessage(toAddress, Optional.ofNullable(pairs), ContentHint.DEFAULT, finalMessage, null, false, false);
        } catch (UntrustedIdentityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }      

    public void recieveMessage(){
        CredentialsProvider creds = new credsProd(this.aci, this.pni, this.USERNAME, this.PASSWORD, this.pniRegistrationId);
        SignalServiceMessageReceiver x = new SignalServiceMessageReceiver(conf, creds, this.PASSWORD, null, false);
        try {
            List<SignalServiceEnvelope> messages = x.retrieveMessages(false, null);
            byte[] content = messages.get(0).getContent();
            System.out.println(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}