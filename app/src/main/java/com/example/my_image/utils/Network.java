package com.example.my_image.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
    public static final int TYPE_WIFI = 0;
    public static final int TYPE_MOBI = 1;
    public static final int TYPE_NOT_CONNECTED = 2;

    public static final int TYPE_STATUS_WIFI = 0;
    public static final int TYPE_STATUS_MOBI = 1;
    public static final int TYPE_STATUS_NOT_CONNECTED = 2;

    private static int getConnectStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBI;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectionStatusString(Context context) {
        int connectivity = Network.getConnectStatus(context);
        int status = 0;
        if (connectivity == Network.TYPE_MOBI) {
            status = TYPE_STATUS_MOBI;
        } else if (connectivity == Network.TYPE_WIFI) {
            status = TYPE_STATUS_WIFI;
        } else if (connectivity == Network.TYPE_NOT_CONNECTED) {
            status = TYPE_STATUS_NOT_CONNECTED;
        }
        return status;
    }
}
