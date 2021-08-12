package com.abhi8422.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity2 extends AppCompatActivity  implements CameraClickListener{
    TextView txtWifiName;
    TextInputLayout textInputLayout;
    TextInputEditText id;
    RecyclerView cameraRecycler;
    Button btnPlay;
    ImageButton btnWifi;
    SharedPreferences preferences,prefCount;
    ArrayList<String> strings=new ArrayList<>();
    ConstraintLayout layout;
    private ConnectivityReceiver receiver;
    String searchID,wifiName;
    int position=-1;
    CameraAdapter adapter;

    private static final String TAG = "MainActivity2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        preferences=getSharedPreferences("URLString",MODE_PRIVATE);
        prefCount=getSharedPreferences("URLStringCounter",MODE_PRIVATE);
        /*IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver=new ConnectivityReceiver();
        this.registerReceiver(receiver, filter);*/
        layout=findViewById(R.id.layout);

        ActivityCompat.requestPermissions(MainActivity2.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 101);
        cameraRecycler =findViewById(R.id.recyclerView);
        id=findViewById(R.id.edtId);
        btnPlay=findViewById(R.id.button2);
        textInputLayout=findViewById(R.id.textField);
        txtWifiName=findViewById(R.id.txtwifiname);
        btnWifi=findViewById(R.id.btnWifi);
        btnWifi.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });

        getPreferences();
        if(strings.size()>0){
            cameraRecycler.setVisibility(View.VISIBLE);
        }

        adapter=new CameraAdapter(strings,this);
        cameraRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        cameraRecycler.setAdapter(adapter);
        textInputLayout.setEndIconOnClickListener(v -> {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogTheme);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.help_infoalert_layout, null);
            dialogBuilder.setView(dialogView);
            AlertDialog dialog=dialogBuilder.create();

            dialog.setButton(BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
        btnPlay.setOnClickListener( v ->  {
            String url=id.getText().toString();
                if (url.isEmpty()){
                    Toast.makeText(this, "Please Enter URL", Toast.LENGTH_SHORT).show();
                }else {
                    WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    wifiName= wifiInfo.getSSID().replace("\"","");
                    if(!wifiName.equals("<unknown ssid>")){
                        txtWifiName.setText(wifiName);
                        saveUrlString(url,wifiName);
                        adapter.onDataAdded();
                    }else {
                        txtWifiName.setText("No Wifi Connection");
                        Toast.makeText(this, "Please connect to Wi-Fi", Toast.LENGTH_SHORT).show();
                    }
                }
        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        wifiName= wifiInfo.getSSID().replace("\"","");
        if(wifiName.equals("<unknown ssid>")){
            txtWifiName.setText("No WiFi Connection");
        }else {
            txtWifiName.setText(wifiName);
        }
    }

    public void saveUrlString(String urlString, String wifiName){

        SharedPreferences.Editor editor= preferences.edit();
        SharedPreferences.Editor countEditor= prefCount.edit();
        int cnt=prefCount.getInt("counter",0);
        String cameraInfo=urlString+"WN"+wifiName;
        if(!strings.contains(cameraInfo)) {
            if ( cnt < 5) {
                cnt = cnt + 1;
                editor.putString(cameraInfo, cameraInfo);
                countEditor.putInt("counter", cnt);
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra("URL", urlString.trim()));
                finish();
            }else{
                AlertDialog dialog=new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setMessage("You can save maximum 5 camera shortcuts.please delete one camera shortcut")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

            }
            /*
                cnt = 1;
                editor.putString(cameraInfo, cameraInfo);
                countEditor.putInt("counter", cnt);
            } else {
                cnt = cnt+1;
                editor.putString(cameraInfo, cameraInfo);
                countEditor.putInt("counter", cnt);
            }*/
            editor.apply();
            countEditor.apply();
        }
    }

    public void deleteUrlString(String cameraInfo){
        SharedPreferences.Editor editor= preferences.edit();
        SharedPreferences.Editor countEditor= prefCount.edit();
        editor.remove(cameraInfo);
        int count=prefCount.getInt("counter",0);
        if(count==0){
            countEditor.putInt("counter", 1);
        }else {
            countEditor.putInt("counter",count-1);
        }
        editor.apply();
        countEditor.apply();
    }

    public List<String> getPreferences() {
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                String sUrl=preferences.getString(entry.getKey(),"");
                if(!sUrl.isEmpty()) {
                    strings.add(preferences.getString(entry.getKey(), ""));
                }
            }catch (Exception e){
                break;
            }
        }
/*
        for (int i=0;i<=5;i++){
            String sUrl=preferences.getString(String.valueOf(i),"");
            if(!sUrl.isEmpty()) {
                strings.add(preferences.getString(String.valueOf(i), ""));
            }
        }*/
        return strings;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(receiver);
    }

    @Override
    public void CameraClick(String cameraUrl) {
        id.setText(cameraUrl.trim());
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("URL", cameraUrl.trim()));
        finish();
    }

    @Override
    public void CameraDelete(String cameraInfo,int position) {
        deleteUrlString(cameraInfo);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.new_game:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}

interface CameraClickListener {
    void CameraClick(String cameraName);
    void CameraDelete(String cameraInfo,int position);
}

 interface OnDataSetChangeListener {
    void onDataRemoved(int position);
    void onDataAdded();

}