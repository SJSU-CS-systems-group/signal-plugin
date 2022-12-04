package com.thesis.app;

import org.whispersystems.signalservice.api.SignalServiceMessageSender.EventListener;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

public class Event implements EventListener{

    @Override
    public void onSecurityEvent(SignalServiceAddress address) {
        return;        
    }
    
}
