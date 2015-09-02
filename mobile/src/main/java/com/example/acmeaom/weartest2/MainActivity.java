package com.example.acmeaom.weartest2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity {
    private Handler uiThread = new Handler(Looper.getMainLooper());
    private GoogleApiClient googleApiClient;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blah);

        GoogleApiClient.Builder b = new GoogleApiClient.Builder(this);
        b.addApi(Wearable.API);
        b.addConnectionCallbacks(googleApiClientConnectionListener);
        b.addOnConnectionFailedListener(googleApiConnectionFailedListener);

        googleApiClient = b.build();
        googleApiClient.connect();

        uiThread.post(watchPinger);
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
                }

                @Override public void onConnectionSuspended(int i) {
                    MainActivity.this.connected = false;
                }
            };

    private int i;
    private final Runnable watchPinger = new Runnable() {
        @Override public void run() {
            PendingResult<NodeApi.GetConnectedNodesResult> cn =
                    Wearable.NodeApi.getConnectedNodes(googleApiClient);
            cn.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                    List<Node> nodes = getConnectedNodesResult.getNodes();
                    for (Node node : nodes) {
                        PendingResult<MessageApi.SendMessageResult> smrpr =
                                Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
                                        "/fromthephone", ("this is from the phone " + i++).getBytes());
                        smrpr.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult result) {
                                debugToast(result.getStatus() + " " + result);
                            }
                        });
                    }
                }
            });

            uiThread.postDelayed(this, 5000);
        }
    };

    private void debugToast(String toastText) {
        debugToast(this, toastText);
    }

    public static void debugToast(Context c, String toastText) {
        Toast.makeText(c, toastText, Toast.LENGTH_LONG).show();
        Log.d("BLAH", toastText);
    }
}
