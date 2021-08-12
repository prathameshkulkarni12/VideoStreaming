package com.abhi8422.videostreaming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class ConnectivityReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(!mWifi.isConnected()){
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Not connected to Wi-Fi")
                    .setMessage("Please check Wi-Fi connection")
                    .create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            //dialog.setCancelable(false);
            dialog.show();
        }
    }
}