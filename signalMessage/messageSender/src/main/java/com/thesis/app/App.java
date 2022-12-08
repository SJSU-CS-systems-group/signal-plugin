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
import java.util.concurrent.TimeoutException;

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
import org.whispersystems.signalservice.api.websocket.WebSocketUnavailableException;
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
    CredentialsProvider creds;
    ProfileKey profileKey;


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
        System.out.println(args[0]);
        String currentObjective = args[0];
        // if(currentObjective.equals("register")){
        example.register();
        // }

        // }
        // example.register();
        // example.sendNewPreKeyBundle();
        Thread.sleep(10000);
        // try {
        //     example.sendMessage();
        // } catch (InvalidInputException e) {
        //     e.printStackTrace();
        // } catch (InvalidCertificateException e) {
        //     e.printStackTrace();
        // }
        try {
            example.recieveMessage();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void saveKeyStore(myKeyStore x) {
        this.prefs.putByteArray("IdentityKeyPublic", x.identityKeyPair.getPublicKey().serialize());
        this.prefs.putByteArray("IdentityKeyPrivate", x.identityKeyPair.getPublicKey().serialize());
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
        saveKeyStore(this.keys);
        // String str = new String(this.keys.identityKeyPair.serialize());
        // prefs.put("OG_IdentityKey", str);
        this.accountManager = new SignalServiceAccountManager(this.conf,
                new DynamicCredentialsProvider(null, null, this.USERNAME, this.PASSWORD,
                        SignalServiceAddress.DEFAULT_DEVICE_ID),
                this.USER_AGENT,
                null,
                true);
        String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODB24Jl3FKMCeqvFIPdEPA4av1pOGcJ8KswISdYhj4w-oivCY7f1t1Zuw8SmEp-P7kshKEvc4Rij4OAdKSfitzMUGyP9E7zbzWoTJq2OwvxYplGwTfsGnQPbdkVj8hWDCo1WMLuEYGH28dBQESEYceGkXpijZdk8clhxiHJwsTRSq371vTg3pEs8YLXh4J36Nxf12MdvpTNtTL1_DSkFy26KUsMXIAAOoUeW2d9ijadTUHOwU4AC-dMBM89DMoNjS-PtADftf24q03beIjH1pLkz9E8KUDc9C6Yyz4THwcyWpkApWdXmr7G9YKfKnUacRUERQNb0ZYCoPe0HMsG7jJR6yegsprGSOVQz68Hoc3u9IEPb2RziVmuZjXwOzd8gJv7BR5neTsVxeayHtUYhGRDQkB4s601si34eN_oiiPkJVB-rxRBF1lcD9KRZWigJLeeNzApB528K0x7QTtkMMQGvGym10TIjE0RC98fvRqGzRmtzH9bTql4Guq0jBUNMIVjixwMoW0mIvKIRYsPP4ae6O49JyMImkEJn3Q4V0DX7rO-qqcfMEP-0d72ZHEqfbU_xHDiLYsaDRqe1EwiTXv-1YMv_DVLABk8WBWEWbzagsIw71Azo4HszNqqXijxtPfwrADj7j6uYwk38Og8jmbDmNMAV_-KrYRYJJgvPv1AUMcTL2uDx4rLptA3eG5F-mV5mYqSJzKTxPn-gewXvGjtS-YWEHmhdzIbhIk3FVLJPCJnvTTj4LsRByH1JWwSH3Bj4LkCPEVuMwxsYRd1NpMVtS6WC0luMOy-QYnhR1UnzttkPGgtojxRy8G5XFfZwyG7fY6Ufjuvv2psY5MoGKKpgEOU5UvkDPRyikpB7AOF2NyzXW_6bRfsMmUYeZnjeYpoiFxvM_FAuMO6IUQKR_Ve9jhswgTx8uCyZg5W-o_Tkl_SsfMeuVfa9J2DIsefAUvxbgPxoIm0lAwfR-yAINRHadu4fdCOPf4VbIJruZSLVnht91Te12AXse_is5-dFRpqwJt3pj6-Nbx6ZLu1KaFcIapDnm-msBYtJa4hytTq9dJr97D-JhiBwJ3Nk8WREplXi8JFwHOQILeSbtj5eodl8Mw60L_NsoutXTWI4p0022b57eq4W6w9wnWl2FowptnLblrfllpqSdPVtL3mYWDEXXt4tRUYTnhfL8XxQ8tIarROCqOVCSLAbzuIEpaAZN4vZTTJ_DecrGW_HaIXuLSv-HHl3NvqYS84ZVdIt6gB9aC-izLTpMWHOQJuKHgfLb90CyBBMHWqH4mgHYZfrXHbr1qiZS1-sP04WByYYts6CrkzFLahTcbV_ZlxpmF2ZqjsQqLNmWSNthOzDT8U8mcHqbwDE-g";
        ServiceResponse<RequestVerificationCodeResponse> hi = this.accountManager.requestSmsVerificationCode(false,
                Optional.ofNullable(signalCaptcha), Optional.ofNullable("x"), Optional.ofNullable("x"));
        System.out.println(hi.getStatus());

        try {
            this.profileKey = myKeyStore.generateProfileKey();
        } catch (InvalidInputException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        this.registrationID = KeyHelper.generateRegistrationId(false);
        this.pniRegistrationId = KeyHelper.generateRegistrationId(false);
        // prefs.put("REGISTRATION_ID", Integer.toString(this.registrationID));
        this.unidentifiedAccessKey = UnidentifiedAccess.deriveAccessKeyFrom(this.profileKey);
        int signalKey = 1324;
        // UUID id = null;
        ServiceResponse<VerifyAccountResponse> example = null;
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter the code you have recieved via sms.");
        String code = in.nextLine();
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
        Optional<VerifyAccountResponse> result = example.getResult();
        this.aci = ACI.parseOrNull(result.get().getUuid());
        this.pni = PNI.parseOrNull(result.get().getPni());
        prefs.putByteArray("ACI", this.aci.toByteArray());
        prefs.putByteArray("PNI", this.pni.toByteArray());
        prefs.putInt("pniRegistrationId", this.pniRegistrationId);
        prefs.putInt("registrationID", this.registrationID);
        creds =  new credsProd(this.aci, this.pni, this.USERNAME, this.PASSWORD, this.pniRegistrationId);
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
        prefs.putInt("signalKey", signalKey);
        try {
            sendNewPreKeyBundle();
        } catch (IOException | InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendNewPreKeyBundle() throws IOException, InvalidKeyException {
        this.accountManager.setPreKeys(ServiceIdType.PNI, this.keys.getIdentityKeyPair().getPublicKey(),
                this.keys.getPreSignedKey(), this.keys.getPreKeysList());
        this.accountManager.setPreKeys(ServiceIdType.ACI, this.keys.getIdentityKeyPair().getPublicKey(),
                this.keys.getPreSignedKey(), this.keys.getPreKeysList());
    }

    public void sendMessage() throws InvalidInputException, InvalidCertificateException{
        SignalServiceDataStore dataStore = new SignalDataStore(this.keys, this.pniRegistrationId);
        WebSocketFactory fact = new webFact("evan", conf, creds);
        SignalWebSocket web = new SignalWebSocket(fact);
        Long envSize = (long) 1000;
        SignalServiceMessageSender sender = new SignalServiceMessageSender(conf, creds, dataStore, null, "message", web, null, null, null, envSize, false);
        long timestamp = System.currentTimeMillis();
        SignalServiceDataMessage.Builder messageBuilder = SignalServiceDataMessage.newBuilder();
        messageBuilder.withTimestamp(timestamp);
        messageBuilder.withBody("hello");
        SignalServiceDataMessage finalMessage = messageBuilder.build();
        SignalServiceAddress toAddress = new SignalServiceAddress(this.pni, "+14088075656");
        byte[] senderCert = UnidentifiedAccess.deriveAccessKeyFrom(this.profileKey);
        UnidentifiedAccess self = new UnidentifiedAccess(this.unidentifiedAccessKey, senderCert);
        UnidentifiedAccessPair pairs = new UnidentifiedAccessPair(null, self);
        try {
            sender.sendDataMessage(toAddress, Optional.ofNullable(pairs), ContentHint.DEFAULT, finalMessage, null, false, false);
        } catch (UntrustedIdentityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }      
    public void printMessage(SignalServiceEnvelope x) {
        byte[] content = x.getContent();
        System.out.println(x.isSignalMessage());
        System.out.println(content);
    }

    public void recieveMessage() throws WebSocketUnavailableException, TimeoutException, IOException{
        WebSocketFactory fact = new webFact("evan", conf, creds);
        SignalWebSocket websocket =  new SignalWebSocket(fact);
        websocket.connect();
        // Optional<SignalServiceEnvelope> env = websocket.readOrEmpty((long)10000,null);
        // byte[] content = env.get().getContent();
        // System.out.println(env.get().isSignalMessage());
        // System.out.println(content);
        
    }
}





    //     SignalServiceMessageReceiver x = new SignalServiceMessageReceiver(conf, creds, null, null, false);
    //     try {
    //         List<SignalServiceEnvelope> messages = x.retrieveMessages(false, null);
    //         byte[] content = messages.get(0).getContent();
    //         System.out.println(content);
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }
