package com.thesis.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.whispersystems.signalservice.internal.ServiceResponse;
import org.whispersystems.signalservice.internal.configuration.SignalCdnUrl;
import org.whispersystems.signalservice.internal.configuration.SignalCdsiUrl;
import org.whispersystems.signalservice.internal.configuration.SignalContactDiscoveryUrl;
import org.whispersystems.signalservice.internal.configuration.SignalKeyBackupServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalProxy;
import org.signal.libsignal.metadata.certificate.InvalidCertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.signal.libsignal.zkgroup.InvalidInputException;
import org.signal.libsignal.zkgroup.profiles.ProfileKey;
import org.whispersystems.libsignal.InvalidKeyException;

import org.whispersystems.libsignal.state.SignalProtocolStore;

import org.signal.libsignal.protocol.util.KeyHelper;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.account.AccountAttributes;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.ServiceIdType;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.api.websocket.WebSocketUnavailableException;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.configuration.SignalServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalStorageUrl;
import org.whispersystems.signalservice.internal.push.RequestVerificationCodeResponse;
import org.whispersystems.signalservice.internal.push.VerifyAccountResponse;
import org.whispersystems.signalservice.internal.util.DynamicCredentialsProvider;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import okhttp3.Dns;
import okhttp3.Interceptor;

/**
 * Hello world!
 *
 */
public class App {
    myKeyStore keys;
    final String URL;
    final String USERNAME;
    final String PASSWORD;
    final String USER_AGENT;
    SignalProtocolStore proto;
    Preferences prefs;
    int registrationID;
    SignalServiceAccountManager accountManager;
    int pniRegistrationId;
    ACI aci;
    PNI pni;
    byte[] unidentifiedAccessKey; 
    ProfileKey profileKey;


    public App() {
        this.URL = "https://textsecure-service.whispersystems.org";
        this.USERNAME = "+14085026204";
        this.PASSWORD = "evanchopra1234";
        this.USER_AGENT = "Mozilla/5.0 (Linux; Android 12; Pixel 6 Build/SD1A.210817.023; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/94.0.4606.71 Mobile Safari/537.36";
        this.prefs = Preferences.userNodeForPackage(App.class).node(App.class.getSimpleName());
    }

    public static void main(String[] args)
            throws InvalidKeyException, KeyStoreException, BackingStoreException, IOException, InterruptedException, TimeoutException {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keyStore = KeyStore.getInstance("BKS");
        App example = new App();
        System.out.println(args[0]);
        String currentObjective = args[0];
        if(currentObjective.equals("register")){
            example.register();
            example.sendNewPreKeyBundle();
        }
        if(currentObjective.equals("listen")){
            example.recieveMessage();
        }
        if(currentObjective.equals("createDummy")){
            App.createSignalEnvelope();
        }
        // example.register();
        // example.sendNewPreKeyBundle();
        // try {
        //     example.sendMessage();
        // } catch (InvalidInputException e) {
        //     e.printStackTrace();
        // } catch (InvalidCertificateException e) {
        //     e.printStackTrace();
        // }
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
        SignalServiceConfiguration conf = buildConf();
        this.accountManager = new SignalServiceAccountManager(conf,
                new DynamicCredentialsProvider(null, null, this.USERNAME, this.PASSWORD,
                        SignalServiceAddress.DEFAULT_DEVICE_ID),
                this.USER_AGENT,
                null,
                true);
        String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODBtDSgsVy5a5CrXuwofVWluKyGiM_ItJA0o2SJhPDhnQwePhNF3OOoMQh7zzYLsrcc3Dk-PfIv-NIY8sjtcQImEfb2rIfT4LWdw4_kOzvPGLPBV6gMmcu360BPSNLqbuF9OTJedfSstasqaC6bh3wIbsvol4gx6KC27Cxj5X-sM6iQdRdyeQ7MttJ8zeFTYMNLP6AYvfP2y-xL5S_IIsgltiOxqjaNNxZX6o5mQeVzx0wxaWGV39wFz9685a6opYSf-2CND1d8Modk2DawVVEP4hzQNa_eBJVv9bcIKUAh3JHVedAO5m0V_yEFXNahRhHcUk730Xds72QjeDEkuS_W-2DGfd5bX2jGnyHJfdu_3wT8iFAJhJm2y4CJYPVrWVmSpHgeQH0WQVyqHaRKtHay7CTplKfl9Pz1Jy0M0A7DtuCKX7fs7Xa7tKz30OpTaEhgvXv07GOkHZc9zMbK5gBiM6GruiWf0t3cfFfhz_wdDIV3NAJfyBXmE39Su4HteB6AXKiOukOBy3_02Zi2nmgRw8juZz7ZSLvBimBMIBPbHYnyi6MdC8c53O1IXgBbn8kSTbflhLwQTgi4X1PWMyYGDShiK5P-4_tFqLAlgGYZD3YFUmBPgi3ovqZ03z0Fqb_g3Dovgaa-fIjhi92hkz6CKmEcsj2XeJMJHeCH6TyyJLkPD9mJsAMi_ujAA6xngdlLyn-lMDSpxJ5Lppm6WFTVEZoMfN5bCmd0abBKGJkk5dSzahGrkMZVA5_-rrQxhr4Rn-7jdh-ikgkMeXZVp-OV7JRFfOO2SfpvpXpIZ5XrrEq_uA1Afu5JdwQ1XsJvC3svKrZeT6Hdj-6mNqRgsg-BtcZ1lwsk9aZ_Kdk0kZ7tnINH3B0hkzNBV9fItX5Pdl7QBiIJJz2p3m4H33pTALvgatYMzYfhgds3dOrvKfUcgmbLf13ZDWq-srAMRYtu2UoaNNq_JcM4SL0Y8autHLjob7PgM6OGW30PW0OWsEwjW3rRK5AEGcUbtgZFdiuPtaT-BG6NiYkb-Wsgks-oQ3FWb7C6NM6ko_aNdreUUiy83lmis1FC-aMqOSLr3-uq1YbwMHdT1UPQv1kulGAHrQurqcSRa41KSNiCNvXKfQ-We9rscxwOxY4NtxtyKdQXqbwrbzz-oqLVnsB_KoDb-sqndPvCXH4hrr8g9B6bVa2NIoJVI4GsmlFqY_0Yn0Ll1we5RZ-CmwDEguWdzhA7zfI19fJ1AxwCIrjm15EJP64OrKmrTpgBMX3GtcZtBKWtVzxkSxCwAehQXsgLVKS0wqLsaRoew-wJzqCMWJ4_PAERXjLVF9IpK7L-a-pL9FlHpZrvMoPedmJH2uz7knNHSoE5KXH9mZw";
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
        prefs.put("ACI", this.aci.toString());
        prefs.put("PNI", this.pni.toString());
        prefs.putInt("pniRegistrationId", this.pniRegistrationId);
        prefs.putInt("registrationID", this.registrationID);
        conf = buildConf();
        this.accountManager = new SignalServiceAccountManager(conf,
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
        SignalServiceConfiguration conf = buildConf();
        CredentialsProvider creds = buildCreds();
        SignalServiceDataStore dataStore = new SignalDataStore(this.keys, this.pniRegistrationId);
        WebSocketFactory fact = new webFact("evan", conf, creds);
        SignalWebSocket web = new SignalWebSocket(fact);
        Long envSize = (long) 1000;
        SignalServiceMessageSender sender = new SignalServiceMessageSender(conf, creds, dataStore, null, "message", web, null, null, null, envSize, false);
        long timestamp = System.currentTimeMillis();
        Method m = null;
        try {
            m = SignalServiceMessageSender.class.getDeclaredMethod("sendMessage");
        } catch (NoSuchMethodException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        m.setAccessible(true);
        try {
            m.invoke(sender, "Green Goblin");
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
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
    
    public CredentialsProvider buildCreds(){
        String aciUnconverted = prefs.get("ACI", "");
        UUID x = UUID.fromString(aciUnconverted);
        System.out.println(aciUnconverted);
        ACI aci = ACI.from(x);
        String pniUnconverted = prefs.get("PNI", "");
        x = UUID.fromString(pniUnconverted);
        System.out.println(pniUnconverted);
        PNI pni = PNI.from(x);
        int pniRegistrationId = prefs.getInt("pniRegistrationId", 0);
        CredentialsProvider creds =  new credsProd(aci, pni, "+14085026204", "evanchopra1234", pniRegistrationId);
        return creds;
    }
    public void printMessage(SignalServiceEnvelope x) {
        byte[] content = x.getContent();
        System.out.println(x.isSignalMessage());
        System.out.println(content);
    }


    public void recieveMessage() throws WebSocketUnavailableException, TimeoutException, IOException{
        SignalServiceConfiguration conf = buildConf();
        CredentialsProvider creds = buildCreds();
        WebSocketFactory fact = new webFact("evan", conf, creds);
        SignalWebSocket websocket =  new SignalWebSocket(fact);
        websocket.connect();
        Optional<SignalServiceEnvelope> env = websocket.readOrEmpty((long)10000, envelope1 -> {
            System.out.println(envelope1);
        });
        byte[] content = env.get().getContent();
        System.out.println(env.get().isSignalMessage());
        System.out.println(content);
        
    }

    public SignalServiceConfiguration buildConf(){
        TrustStore TRUST_STORE = new MyTrustStoreImpl();
        SignalServiceUrl[] allUrls = new SignalServiceUrl[] { new SignalServiceUrl("https://textsecure-service.whispersystems.org", TRUST_STORE) };
        Map<Integer, SignalCdnUrl[]> signalCDNMap = new HashMap<Integer, SignalCdnUrl[]>();
        signalCDNMap.put(0, new SignalCdnUrl[] { new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE) });
        signalCDNMap.put(2, new SignalCdnUrl[] { new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE) });
        SignalKeyBackupServiceUrl[] SKBS = {};
        SignalStorageUrl[] SSU = {};
        SignalCdsiUrl[] SCU = {};
        List<Interceptor> Int_ceptor = new ArrayList<Interceptor>();
        Optional<Dns> dns = Optional.empty();
        Optional<SignalProxy> proxy = Optional.empty();
        SignalServiceConfiguration conf = new SignalServiceConfiguration(allUrls, signalCDNMap,
                new SignalContactDiscoveryUrl[] {
                        new SignalContactDiscoveryUrl("https://api.directory.signal.org", TRUST_STORE) },
                SKBS, SSU, SCU, Int_ceptor, dns, proxy, null);
        return conf;
    }

    public static void createSignalEnvelope(){
        Random rd = new Random();

        byte[] random = new byte[16];
        rd.nextBytes(random);
        try {
            SignalServiceEnvelope x = new SignalServiceEnvelope(random, (long)222);
            byte[] result = x.serialize();
            Path path = Paths.get("/bytes");
            Files.write(path, result);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
