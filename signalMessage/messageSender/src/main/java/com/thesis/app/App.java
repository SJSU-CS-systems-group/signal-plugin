package com.thesis.app;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.whispersystems.signalservice.internal.configuration.SignalCdnUrl;
import org.whispersystems.signalservice.internal.configuration.SignalContactDiscoveryUrl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
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
    public static void main( String[] args ) throws InvalidKeyException, KeyStoreException
    {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keyStore = KeyStore.getInstance("BKS");

        final String     URL         = "https://textsecure-service.whispersystems.org";
        final TrustStore TRUST_STORE = new MyTrustStoreImpl();
        final String     USERNAME    = "+14085026204";
        final String     PASSWORD    = "evanchopra1234";
        final String     USER_AGENT  = "computer";
        SignalServiceConfiguration x = new SignalServiceConfiguration( new SignalServiceUrl[]{new SignalServiceUrl(URL, TRUST_STORE)},
        new SignalCdnUrl[]{new SignalCdnUrl("https://cdn.signal.org", TRUST_STORE)},
        new SignalContactDiscoveryUrl[]{new SignalContactDiscoveryUrl( "https://api.directory.signal.org", TRUST_STORE)});
        System.out.println(TRUST_STORE.getKeyStoreInputStream());
        SignalServiceAccountManager accountManager = new SignalServiceAccountManager(x, null,
                                                                                    USERNAME, PASSWORD, USER_AGENT);
        System.out.println("Here");
        // try{
        try {
            accountManager.requestSmsVerificationCode(false, Optional.fromNullable("signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AIIukzgdwp4qHvtSRJfT5Jjhswg5PGnLSAXMS-eI5qMMsYM_Mraw1cevZu764tV4ZuaRs6k5Cc7VOkJ-KU86LN6jOo7LNmdTDYltO_-gcFXN4kGvzvptCYTq0jcWv_z6MkCp4hXyzVwerRaaI359TxHwjPk38e8AtnmAnojpKQj8Qp56S0xqU5Z3_MXiaDoyE1C9D0t28SZglftH1qWlkpZDUdcoDY01w6P2J8B8vnGGL9_iU3fBRfPNb8La8hlIfrFULvBLsb-G_M7ytyR2QcrgYUF2VCultGS6rowc5ZvVEitTfhNYobU76DF0P6-KKpvYxEC-PziRQdVaVdoioNFKIAXZEGW1u25eERrKaRmY8lhMsIRi0kyYoH32H8rt98x-es9F0Jt9YBQ6e9n0Pw4iivRyzDieul6aVIOpq-bdSAtvikhCFRvTuXTmrrg_fM15bR5tv7oWsEbOi5IG2uICvEU4cG79-1PGQ6OjtowkPCD9voTrhm7-NN-Vjk6WZmcmHR0ytrdRMcmHkoS11ZqNiPzecqoh5-rbhRW76jIJuQOqL1tScYZ5JEidwqLswmUyLnJBOJPCOFF7hGQO5FQ9m1NTXmdn4DZ6PsGye4w4mFCYyXEVA7ru5Ld_jGU0LQOS3b41MiWYeo9oBCcUT6slodqPedR4A6Gb3B8rNGHfcLc3aq50xceLuYuNMBgCzNmEJ25EbPZlKSfZS9t0yntTvez-eGrY_hG5uUvycfR1ArbhTyM_Dh1obPS7EDOhUh23sVK9iNYDf3SyJMbIIoeg7FPhpXYT_cYvY7t684nhPMzRuqOkqxBF3xQTwOLkcZoeOqzzku80D6liQRB3yMlnMAAAYL6Lr47vtxw24U_fWB3myjbvWDrcL4joKwepD0q28Ws0fRDJSZ8imySmbX5hsNfVdAd7C4LDAxkpm-MDUHuivR9nSzokmtafaQB0A8C8s18N7OvX0x-8q13Ognb-7z75jGjPhCsNT1PO9U1Zl5J6gyoCL2FNJcuxwi24HCZrf23fOdGEaZPNCmRPMD6-2OzE4dcVhkAhu1JtJcSbX8WoOE1JXR9mQfwfIrevInf32IqiRBkceiBzIUtcrEQpM7PNQDudHVTxGhUDcsmWMaMp1Y7IRKt7eDAHK0JyKS-4b9M9Es1NbgpkBY0EMsmqZRYeDbSiAGv2OGaS_r2Nh9HRlETgJi_ceHNa8d-EOpvLY40qbbPntRVhjxDdIbAnRbZFISdupCC_7UctjAwHcBvTRJrFTy5o5d7geAeW6fCTdQqhDHynd94c7DGoCtiCqDuK-hFfCUURV2dLQ1yhkGF7HOoo7SHcYxU4et1Kc--nuLdvnkg2KmiKutCXPrbCtIF4xmU1c-iEaO-CT8efdSDQHtew__GUsB0ZfEgLzqk4zpAjOr_9vFNhrfri3AiPexwsxbcP4CiFV3OU_CUIlLpp6o9g6kusLhaUVf8nPYcpbPlJjgpXtSr9L-VDLwg2DxosDPzdk8iZEnqDrGA69PDxNE0j1r_uzEu7V2vXtlKRoVV0bGf00-00-kmBWHKymE0GN9UzddCBBiUEGQS1m9z1cWBPRlfxgb8TA8AVA8YshZ0QW7qgcwTbMK6J7xSNcC3QVnHxPmsFasrQNGLuaffqgy6HWyWnzAGjTZ_0vLuRbRcQFGaGwbUwOhzzR_WTMJuVqGa1TQ"), Optional.fromNullable("x"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Scanner in = new Scanner(System.in);
 
        String code = in.nextLine();
        byte[] profileKey = Util.getSecretBytes(32);
        Random random = new Random();
        int installID = random.nextInt(10000);
        UUID id;
        try {
            id = accountManager.verifyAccountWithCode(code, null, installID, true, null, profileKey, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // } catch(Exception e){
        //     System.out.println(e);
        // }

        // try {
        //     TimeUnit.SECONDS.sleep(100);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // accountManager.setGcmId(Optional.of(GoogleCloudMessaging.getInstance(this).register(REGISTRATION_ID)));
        // accountManager.setPreKeys(identityKey.getPublicKey(), lastResortKey, signedPreKeyRecord, oneTimePreKeys);
        System.out.println( "Hello World!" );
    }
}
