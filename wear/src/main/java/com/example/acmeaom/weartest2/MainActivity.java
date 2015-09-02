package com.example.acmeaom.weartest2;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity {
    private GoogleApiClient googleApiClient;
    private boolean connected;

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);

        GoogleApiClient.Builder b = new GoogleApiClient.Builder(this);
        b.addApi(Wearable.API);
        b.addConnectionCallbacks(googleApiClientConnectionListener);
        b.addOnConnectionFailedListener(googleApiConnectionFailedListener);

        googleApiClient = b.build();
        googleApiClient.connect();
    }

    private final GoogleApiClient.OnConnectionFailedListener googleApiConnectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override public void onConnectionFailed(ConnectionResult connectionResult) {
                    debugToast(""+connectionResult);
                }
            };

    private final GoogleApiClient.ConnectionCallbacks googleApiClientConnectionListener =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override public void onConnected(Bundle bundle) {
                    MainActivity.this.connected = true;
                    Wearable.MessageApi.addListener(googleApiClient, messageListener);
                }

                @Override public void onConnectionSuspended(int i) {
                    MainActivity.this.connected = false;
                }
            };

    private MessageApi.MessageListener messageListener = new MessageApi.MessageListener() {
        @Override public void onMessageReceived(final MessageEvent messageEvent) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    debugToast("message listener received: " + new String(messageEvent.getData()));
                }
            });
        }
    };

    private void debugToast(String toastText) {
        debugToast(this, toastText);
    }

    public static void debugToast(Context c, String toastText) {
        Toast.makeText(c, toastText, Toast.LENGTH_LONG).show();
        Log.d("BLAH", toastText);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
