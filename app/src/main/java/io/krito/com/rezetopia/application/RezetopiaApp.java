package io.krito.com.rezetopia.application;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.krito.com.rezetopia.model.operations.UserOperations;
import io.krito.com.rezetopia.receivers.ConnectivityReceiver;

public class RezetopiaApp extends Application{

    private static RezetopiaApp mInstance;
    private ConnectivityReceiver receiver;
    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, filter);

        UserOperations.setRequestQueue(getRequestQueue());
    }

    public static synchronized RezetopiaApp getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(this);

        return requestQueue;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(receiver);
    }
}
