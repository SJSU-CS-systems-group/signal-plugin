package com.thesis.app;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.Security;
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
import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.signal.libsignal.zkgroup.InvalidInputException;
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
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.account.AccountAttributes;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.push.ServiceIdType;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.api.storage.SignalAccountRecord;
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
import okhttp3.Interceptor;
import okhttp3.WebSocket;

/**
 * Hello world!
 *
 */
public class App 
{
    myKeyStore keys;
    final String     URL;
    final TrustStore TRUST_STORE;
    final String     USERNAME;
    final String     PASSWORD;
    final String     USER_AGENT;
    SignalProtocolStore proto;
    SignalServiceUrl[] allUrls;
    SignalServiceConfiguration conf;
    Preferences prefs;
    int registrationID;
    SignalServiceAccountManager accountManager;

    public App(){
        this.URL         = "https://textsecure-service.whispersystems.org";
        this.TRUST_STORE = new MyTrustStoreImpl();
        this.USERNAME    = "+14085026204";
        this.PASSWORD    = "evanchopra1234";
        this.USER_AGENT  = "Mozilla/5.0 (Linux; Android 12; Pixel 6 Build/SD1A.210817.023; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/94.0.4606.71 Mobile Safari/537.36";
        // SignalServiceUrl[] signalServiceUrls,
        //                             Map<Integer, SignalCdnUrl[]> signalCdnUrlMap,
        //                             SignalContactDiscoveryUrl[] signalContactDiscoveryUrls,
        //                             SignalKeyBackupServiceUrl[] signalKeyBackupServiceUrls,
        //                             SignalStorageUrl[] signalStorageUrls,
        //                             SignalCdsiUrl[] signalCdsiUrls,
        //                             List<Interceptor> networkInterceptors,
        //                             Optional<Dns> dns,
        //                             Optional<SignalProxy> proxy,
        //                             byte[] zkGroupServerPublicParams)
        this.allUrls = new SignalServiceUrl[]{new SignalServiceUrl(this.URL, this.TRUST_STORE)};
        Map<Integer, SignalCdnUrl[]> signalCDNMap = new HashMap<Integer, SignalCdnUrl[]>();
        signalCDNMap.put(0, new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)});
        signalCDNMap.put(2, new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)});
        SignalKeyBackupServiceUrl[] SKBS = {};
        SignalStorageUrl[] SSU = {};
        SignalCdsiUrl[] SCU = {};
        List<Interceptor> Int_ceptor = new ArrayList<Interceptor>();
        Optional<Dns> dns = Optional.empty();
        Optional<SignalProxy> proxy = Optional.empty();
        this.conf = new SignalServiceConfiguration(allUrls, signalCDNMap, new SignalContactDiscoveryUrl[]{new SignalContactDiscoveryUrl( "https://api.directory.signal.org", TRUST_STORE)}, SKBS, SSU, SCU, Int_ceptor, dns, proxy, null);
        // this.conf = new SignalServiceConfiguration( new SignalServiceUrl[]{new SignalServiceUrl(URL, TRUST_STORE)},
        // new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)},
        // new SignalContactDiscoveryUrl[]{new SignalContactDiscoveryUrl( "https://api.directory.signal.org", TRUST_STORE)});
        this.prefs = Preferences.userNodeForPackage(App.class).node(App.class.getSimpleName());
    }
    public static void main( String[] args ) throws InvalidKeyException, KeyStoreException, BackingStoreException, IOException, InterruptedException
    {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keyStore = KeyStore.getInstance("BKS");
        // UUID iddd = UUID.fromString("496539c2-62ae-40db-b844-d7d36db92eb4");
        App example = new App();
        // UUID id = UUID.fromString(example.prefs.get("UUID", ""));
        // SignalServiceAccountManager accountManager = new SignalServiceAccountManager(example.conf, id,
                                                //   example. USERNAME, example.PASSWORD, example.USER_AGENT);
        example.register();
        // TimeUnit.MINUTES.sleep(1);
        example.sendNewPreKeyBundle();
        // example.sendMessage("hello");

    }

    public void register() throws BackingStoreException, UnsupportedEncodingException{
        prefs.clear();
        try {
            this.keys = new myKeyStore();
        } catch (java.security.InvalidKeyException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();  
        } catch (InvalidKeyException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // String str = new String(this.keys.identityKeyPair.serialize());
        // prefs.put("OG_IdentityKey", str);
        this.accountManager =  new SignalServiceAccountManager(this.conf,
        new DynamicCredentialsProvider(null, null, this.USERNAME, this.PASSWORD, SignalServiceAddress.DEFAULT_DEVICE_ID),
        this.USER_AGENT,
        null,
        true);
        String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODCWYW17XYK7ICQC04eJErG9ygW66g5TZvgpeF7EribmszTRkOWrJXWZqhc-nPft35L0O2e3hkBJB7krljpAcjVqdSjAU2uNm3GcNVKorpNGqt0-Sq6wVShydP5EbL7I4PLROo4K_1GHwHxFZdFo33SfJFh98nS8TeXomb0CliN-3uE7YgSbv4-d6aRjdv_6c8agMkx8awWpbSYDRq8PiBwA-FYc49CIS3F4__891O3Mh5SUngV824N_ZAqAIqE2yfxkC6DMSHX5aZOEgc2rCMKYyW6J6AKjPAEmR13S6EEyQwg1dFxyi96RvYK6mF3g23CA0FgDHvBUfNkzXTWN5RPm-ipm7Hi0C631QHtsMRka3yADg5qrlHdoWDmuYPt-fAfCM3igHe3e1bpT7DJ-8ePuUw3Z5253YNsi5JH69vtFRVwtqwpDxJYbNFY3g7aN1b3Gk4WV4H9l81rYBg46kTZ_Uu4M6sPS_O-dgfrvmd9ssc7LZ04N9a8_RxlYVKeSpurvPNLG74Q_GYhkDT6M5ggeBa1dh18YdKxqNxzLCh9ABhXSKC8Xat2OAljfR8KaC2XB8ZX7Go0saIaZLTqsJVfqef3i4t1lnlxsBw01FuM28srHzzRp7hFS3lQblTN7XrpYWoGszxLhGSGCbr7VNyGHaseSBZQVUwuaeotNGlga3VGvzDFu0kPpEaxemkzZJtaKFoQd68eKOXWM9ZjOqVek0YOpDQWAW7tARyadnfnnV28RXqbRbVnTW25QIWPQH10uWNnYmI2uEMobP_e0TWAhR4NITx3VI74bZ6IAkhs_calxokBchrogzgfBaMbWA-ySDPGjQIEDcfmUCqiJGgLi1lX99ugUVrp4Ge0ZObuNSeE5e7shjw4uEXLQG1amM9mXA6RM2YUvTZJpUV0HvWYx5z3d337vO3jAVc5Dl5-IUARDSllM_-rHqOB5jngIiU5tM40DoXsVGpnjqdbLARhzqzz6eLyxzMmWDIkYhz1Ydzhib2_qPMcuT5K0A9zL62tZ-gjyg_O0mKAAmu_NYe9Ef6VujdyQs5vPrfKUBLAkmiwcVA1nZhlUCyN5A-y4Lgx00I9txf9JzfwyhGjnrjQpDtm5z2vvMHRlH-qNA7QZtWlXeNLfzJ0VjoaSgadU-0ms-7BtouPF5aja_2mXmj0zIP-MtYe8o7MGJKstYiup1hYg_jM--d7mzVmFs7E08Q0xhGB46vGWo-bbQ5vIR27MEhYjBySw6gdP4vK6Ropjw16Dg2_yoYkdSrVplZ96yY05uQcXZDi5rOIfiLmo8BdBWrIX7kN6bzEoh2DJr6KIpq0uMhWid0x-wsTIq0ugeRPganR2kRjWmMnoAoBhBdl6ATAo6A";
        ServiceResponse<RequestVerificationCodeResponse> hi = this.accountManager.requestSmsVerificationCode(false, Optional.ofNullable(signalCaptcha), Optional.ofNullable("x"), Optional.ofNullable("x"));
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
        // prefs.put("REGISTRATION_ID", Integer.toString(this.registrationID));
        byte[] unidentifiedAccessKey = UnidentifiedAccess.deriveAccessKeyFrom(profileKey);
        int signalKey = 1324;
        UUID id = null;
        ServiceResponse<VerifyAccountResponse> example = null;
        example = this.accountManager.verifyAccount(code, signalKey, true, unidentifiedAccessKey, true,  new AccountAttributes.Capabilities(false,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        false,
        false), true, registrationID);
        System.out.println(example.getResult());
        Optional<VerifyAccountResponse> result = example.getResult();
        ACI aci = ACI.parseOrNull(result.get().getUuid());
        PNI pni = PNI.parseOrNull(result.get().getPni());
        System.out.println("ACI" + aci);
        System.out.println("PNI" + pni);
        this.accountManager =  new SignalServiceAccountManager(this.conf,
        new DynamicCredentialsProvider(aci, pni, this.USERNAME, this.PASSWORD, SignalServiceAddress.DEFAULT_DEVICE_ID),
        this.USER_AGENT,
        null,
        true);


        // prefs.put("UUID", id.toString());
        // prefs.put("SIGNALING_KEY", Integer.toString(signalKey));
        System.out.println(example);
    }

    public void sendNewPreKeyBundle() throws IOException, InvalidKeyException {
        // String key = prefs.get("OG_IdentityKey", "");
        // System.out.println(key);
        // IdentityKeyPair IdKey = new IdentityKeyPair(key.getBytes("UTF-8"));
        // this.keys = new myKeyStore(IdKey);\\
        this.accountManager.setPreKeys(ServiceIdType.PNI, this.keys.getIdentityKeyPair().getPublicKey(), this.keys.signedPreKey, this.keys.preKeys);
    }

    // public void sendMessage(String message){
    //     IdentityKeyPair newlyGenerated = KeyHelper.generateIdentityKeyPair();
    //     int regist = Integer.parseInt(prefs.get("REGISTRATION_ID", ""));
    //     UUID id = UUID.fromString(prefs.get("UUID", ""));
    //     System.out.println(id);
    //     proto = new InMemorySignalProtocolStore(newlyGenerated, regist);
    //     Optional<SignalServiceMessagePipe> pipe = Optional.absent();
    //     SignalServiceMessageSender sender = new SignalServiceMessageSender(this.conf, id, this.USERNAME , this.PASSWORD, proto, this.USER_AGENT, false, pipe, pipe, null);
    //     long timestamp = 21312432;
    //     Optional<UnidentifiedAccessPair> empty = Optional.absent();
    //     try {
    //         sender.sendMessage(new SignalServiceAddress(null, "+14088075656"), empty, new SignalServiceDataMessage(21312432, "Hello World"));
    //     } catch (UntrustedIdentityException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //     // SignalServiceMessageReceiver x = new SignalServiceMessageReceiver(config, null, name, null, null)
    // }
}
