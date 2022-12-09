package com.thesis.app;

public class sample {
//     public void register() throws BackingStoreException, UnsupportedEncodingException {
//         prefs.clear();
//         try {
//             this.keys = new myKeyStore();
//         } catch (java.security.InvalidKeyException e1) {
//             e1.printStackTrace();
//         } catch (InvalidKeyException e1) {
//             e1.printStackTrace();
//         }
//         saveKeyStore(this.keys);
//         // String str = new String(this.keys.identityKeyPair.serialize());
//         // prefs.put("OG_IdentityKey", str);
//         SignalServiceConfiguration conf = App.buildConf();
//         this.accountManager = new SignalServiceAccountManager(conf,
//                 new DynamicCredentialsProvider(null, null, this.USERNAME, this.PASSWORD,
//                         SignalServiceAddress.DEFAULT_DEVICE_ID),
//                 this.USER_AGENT,
//                 null,
//                 true);
//         String signalCaptcha = "signal-recaptcha-v2.6LfBXs0bAAAAAAjkDyyI1Lk5gBAUWfhI_bIyox5W.registration.03AEkXODB24Jl3FKMCeqvFIPdEPA4av1pOGcJ8KswISdYhj4w-oivCY7f1t1Zuw8SmEp-P7kshKEvc4Rij4OAdKSfitzMUGyP9E7zbzWoTJq2OwvxYplGwTfsGnQPbdkVj8hWDCo1WMLuEYGH28dBQESEYceGkXpijZdk8clhxiHJwsTRSq371vTg3pEs8YLXh4J36Nxf12MdvpTNtTL1_DSkFy26KUsMXIAAOoUeW2d9ijadTUHOwU4AC-dMBM89DMoNjS-PtADftf24q03beIjH1pLkz9E8KUDc9C6Yyz4THwcyWpkApWdXmr7G9YKfKnUacRUERQNb0ZYCoPe0HMsG7jJR6yegsprGSOVQz68Hoc3u9IEPb2RziVmuZjXwOzd8gJv7BR5neTsVxeayHtUYhGRDQkB4s601si34eN_oiiPkJVB-rxRBF1lcD9KRZWigJLeeNzApB528K0x7QTtkMMQGvGym10TIjE0RC98fvRqGzRmtzH9bTql4Guq0jBUNMIVjixwMoW0mIvKIRYsPP4ae6O49JyMImkEJn3Q4V0DX7rO-qqcfMEP-0d72ZHEqfbU_xHDiLYsaDRqe1EwiTXv-1YMv_DVLABk8WBWEWbzagsIw71Azo4HszNqqXijxtPfwrADj7j6uYwk38Og8jmbDmNMAV_-KrYRYJJgvPv1AUMcTL2uDx4rLptA3eG5F-mV5mYqSJzKTxPn-gewXvGjtS-YWEHmhdzIbhIk3FVLJPCJnvTTj4LsRByH1JWwSH3Bj4LkCPEVuMwxsYRd1NpMVtS6WC0luMOy-QYnhR1UnzttkPGgtojxRy8G5XFfZwyG7fY6Ufjuvv2psY5MoGKKpgEOU5UvkDPRyikpB7AOF2NyzXW_6bRfsMmUYeZnjeYpoiFxvM_FAuMO6IUQKR_Ve9jhswgTx8uCyZg5W-o_Tkl_SsfMeuVfa9J2DIsefAUvxbgPxoIm0lAwfR-yAINRHadu4fdCOPf4VbIJruZSLVnht91Te12AXse_is5-dFRpqwJt3pj6-Nbx6ZLu1KaFcIapDnm-msBYtJa4hytTq9dJr97D-JhiBwJ3Nk8WREplXi8JFwHOQILeSbtj5eodl8Mw60L_NsoutXTWI4p0022b57eq4W6w9wnWl2FowptnLblrfllpqSdPVtL3mYWDEXXt4tRUYTnhfL8XxQ8tIarROCqOVCSLAbzuIEpaAZN4vZTTJ_DecrGW_HaIXuLSv-HHl3NvqYS84ZVdIt6gB9aC-izLTpMWHOQJuKHgfLb90CyBBMHWqH4mgHYZfrXHbr1qiZS1-sP04WByYYts6CrkzFLahTcbV_ZlxpmF2ZqjsQqLNmWSNthOzDT8U8mcHqbwDE-g";
//         ServiceResponse<RequestVerificationCodeResponse> hi = this.accountManager.requestSmsVerificationCode(false,
//                 Optional.ofNullable(signalCaptcha), Optional.ofNullable("x"), Optional.ofNullable("x"));
//         System.out.println(hi.getStatus());

//         }
        

//     public void verfiyACC(){

//         try {
//             this.profileKey = myKeyStore.generateProfileKey();
//         } catch (InvalidInputException e1) {
//             // TODO Auto-generated catch block
//             e1.printStackTrace();
//         }
//         this.registrationID = KeyHelper.generateRegistrationId(false);
//         this.pniRegistrationId = KeyHelper.generateRegistrationId(false);
//         // prefs.put("REGISTRATION_ID", Integer.toString(this.registrationID));
//         this.unidentifiedAccessKey = UnidentifiedAccess.deriveAccessKeyFrom(this.profileKey);
//         int signalKey = 1324;
//         // UUID id = null;
//         ServiceResponse<VerifyAccountResponse> example = null;
//         Scanner in = new Scanner(System.in);
//         System.out.println("Please enter the code you have recieved via sms.");
//         String code = in.nextLine();
//         example = this.accountManager.verifyAccount(code, signalKey, true, unidentifiedAccessKey, true,
//                 new AccountAttributes.Capabilities(false,
//                         true,
//                         false,
//                         true,
//                         true,
//                         true,
//                         true,
//                         true,
//                         false,
//                         false),
//                 true, this.pniRegistrationId);
//         Optional<VerifyAccountResponse> result = example.getResult();
//         this.aci = ACI.parseOrNull(result.get().getUuid());
//         this.pni = PNI.parseOrNull(result.get().getPni());
//         prefs.put("ACI", this.aci.toString());
//         prefs.put("PNI", this.pni.toString());
//         prefs.putInt("pniRegistrationId", this.pniRegistrationId);
//         prefs.putInt("registrationID", this.registrationID);
//         SignalServiceConfiguration conf = App.buildConf();
//         this.accountManager = new SignalServiceAccountManager(conf,
//                 new DynamicCredentialsProvider(aci, pni, this.USERNAME, this.PASSWORD,
//                         SignalServiceAddress.DEFAULT_DEVICE_ID),
//                 this.USER_AGENT,
//                 null,
//                 true);

//         try {
//             this.accountManager.setAccountAttributes(null, this.registrationID, true, null, null, unidentifiedAccessKey,
//                     true,
//                     new AccountAttributes.Capabilities(false,
//                             true,
//                             false,
//                             true,
//                             true,
//                             true,
//                             true,
//                             true,
//                             false,
//                             false),
//                     true, null, this.pniRegistrationId);
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//         prefs.putInt("signalKey", signalKey);
//         try {
//             sendNewPreKeyBundle();
//         } catch (IOException | InvalidKeyException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }

    
//     public void sendNewPreKeyBundle() throws IOException, InvalidKeyException {
//         this.accountManager.setPreKeys(ServiceIdType.PNI, this.keys.getIdentityKeyPair().getPublicKey(),
//                 this.keys.getPreSignedKey(), this.keys.getPreKeysList());
//         this.accountManager.setPreKeys(ServiceIdType.ACI, this.keys.getIdentityKeyPair().getPublicKey(),
//                 this.keys.getPreSignedKey(), this.keys.getPreKeysList());
//     }
}
