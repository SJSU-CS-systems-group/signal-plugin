package com.thesis.app;

import java.util.Optional;

import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.websocket.WebSocketConnection;

public class webFact implements WebSocketFactory {
    String name;
    SignalServiceConfiguration conf;
    CredentialsProvider creds;

    public webFact(String name, SignalServiceConfiguration conf, CredentialsProvider creds ) {
        this.name =  name;
        this.conf = conf;
        this.creds = creds;

    }

    @Override
    public WebSocketConnection createWebSocket() {
        WebSocketConnection x = new WebSocketConnection("evan", this.conf, Optional.of(this.creds), "message", null, "", false);
        return x;
    }

    @Override
    public WebSocketConnection createUnidentifiedWebSocket() {
        WebSocketConnection x = new WebSocketConnection("evan", this.conf, Optional.of(creds), "message", null, "", false);
        return x;
    }
    
    
}
