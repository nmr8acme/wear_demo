package com.example.acmeaom.weartest2;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearMessageListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        MainActivity.debugToast(this, "service received: " + new String(messageEvent.getData()));
    }
}
