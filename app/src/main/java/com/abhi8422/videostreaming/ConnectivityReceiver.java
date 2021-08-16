package com.abhi8422.videostreaming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

public class ConnectivityReceiver  extends BroadcastReceiver {
NetworkChangeListener listener;

    public ConnectivityReceiver(NetworkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                listener.showNetworkAlert();
            }else {
                listener.dismissNetworkAlert();
            }
        }else {
          listener.showNetworkAlert();
        }

    }


}