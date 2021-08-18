package com.abhi8422.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity  implements CameraClickListener,NetworkChangeListener{
    TextView txtWifiName;
    TextInputLayout textInputLayout;
    TextInputEditText id;
    RecyclerView cameraRecycler;
    Button btnView,btnShortcutView;
    ImageButton btnWifi;
    SharedPreferences preferences,prefCount;
    ArrayList<String> strings=new ArrayList<>();
    ConstraintLayout layout;
    private ConnectivityReceiver receiver;
    String wifiName;
    boolean wifiCheck,expandCheck1=true,expandCheck2=true,wifiExpandCheck1=true,
            wifiExpandCheck2=true,wifiExpandCheck3=true,visibility=true;
    CameraAdapter adapter;
    AlertDialog dialog,netWorkDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        preferences=getSharedPreferences("URLString",MODE_PRIVATE);
        prefCount=getSharedPreferences("URLStringCounter",MODE_PRIVATE);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver=new ConnectivityReceiver(this);
        this.registerReceiver(receiver, filter);

        layout=findViewById(R.id.layout);

        ActivityCompat.requestPermissions(MainActivity2.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        cameraRecycler =findViewById(R.id.recyclerView);
        id=findViewById(R.id.edtId);
        btnView =findViewById(R.id.button2);
        btnShortcutView=findViewById(R.id.btnShortcut);
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
          showHelpAlert();
        });

        netWorkDialog=new AlertDialog.Builder(this)
                .setTitle("Network Information")
                .create();

        btnView.setOnClickListener(v ->  {
            String url=id.getText().toString();
            if (url.isEmpty()){
                Toast.makeText(MainActivity2.this, "Please Enter URL", Toast.LENGTH_SHORT).show();
            }else {
                if(!wifiName.replace("\"","").equals("<unknown ssid>")){
                    txtWifiName.setText(wifiName);
                    startActivity(new Intent(MainActivity2.this, MainActivity.class)
                            .putExtra("URL",url.trim()));
                    finish();
                }else {
                    txtWifiName.setText("No WiFi Connection");
                    Toast.makeText(MainActivity2.this, "Please connect to Wi-Fi", Toast.LENGTH_SHORT).show();
                }
            }
        } );

        btnShortcutView.setOnClickListener(v -> {
            String url=id.getText().toString();
            if (url.isEmpty()){
                Toast.makeText(MainActivity2.this, "Please Enter URL", Toast.LENGTH_SHORT).show();
            }else {
                /*WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                wifiName= wifiInfo.getSSID().replace("\"","");*/
                if(!wifiName.replace("\"","").equals("<unknown ssid>")){
                    txtWifiName.setText(wifiName);
                    saveUrlString(url.trim(),wifiName);
                    adapter.onDataAdded();
                }else {
                    txtWifiName.setText("No WiFi Connection");
                    Toast.makeText(MainActivity2.this, "Please connect to Wi-Fi", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibility=true;
       WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        wifiName= wifiInfo.getSSID().replace("\"","");
        if(wifiName.replace("\"","").equals("<unknown ssid>")){
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
        else {
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("URL", urlString.trim()));
            finish();
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

        String[] wifiNameS = cameraUrl.split("WN");
        if(wifiName.replace("\"","").equals("<unknown ssid>")) {
            Toast.makeText(this, "Please connect to WiFi", Toast.LENGTH_SHORT).show();
        }else if(!wifiName.replace("\"","").equals(wifiNameS[1])) {
            Toast.makeText(this, "WiFi is not connected to camera", Toast.LENGTH_SHORT).show();
        }else {
            id.setText(wifiNameS[0].trim());
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("URL", wifiNameS[0].trim()));
            finish();
        }
    }

    @Override
    public void CameraDelete(String cameraInfo,int position) {
        deleteUrlString(cameraInfo);
    }

    public void close(MenuItem item) {
      dialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.help:
                showWifiHelpAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelpAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help_infoalert_layout, null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();

        ImageView downArrow1=dialogView.findViewById(R.id.downarrow1);
        ImageView downArrow2=dialogView.findViewById(R.id.downarrow2);


        downArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck1){
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_up);
                    dialogView.findViewById(R.id.linear).setVisibility(View.VISIBLE);
                    expandCheck1=false;
                }else {
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_down);
                    dialogView.findViewById(R.id.linear).setVisibility(View.GONE);
                    expandCheck1=true;
                }
            }
        });
        downArrow2.setOnClickListener(v1 ->{
            if (expandCheck2){
                downArrow2.setImageResource(R.drawable.ic_arrow_drop_up);
                dialogView.findViewById(R.id.linear2).setVisibility(View.VISIBLE);
                expandCheck2=false;
            }else {
                downArrow2.setImageResource(R.drawable.ic_arrow_drop_down);
                dialogView.findViewById(R.id.linear2).setVisibility(View.GONE);
                expandCheck2=true;
            }
        } );

        dialog.show();

    }

    public void showWifiHelpAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.wifi_help_alert_layout, null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();
       /* SpannableString spannableString = new SpannableString(getString(R.string.wifiAns1));
        SpannableString spannableString2 = new SpannableString(getString(R.string.wifiAns3));
        Drawable d = AppCompatResources.getDrawable(this,R.drawable.wifiicon);
        Drawable d2 = AppCompatResources.getDrawable(this,R.drawable.shortcutbtn);

        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(span, spannableString.toString().indexOf("@"),  spannableString.toString().indexOf("@")+1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        d2.setBounds(0, 0, d2.getIntrinsicWidth(), d2.getIntrinsicHeight());
        ImageSpan span2 = new ImageSpan(d2, ImageSpan.ALIGN_BOTTOM);
        spannableString2.setSpan(span2, spannableString2.toString().indexOf("@"),  spannableString2.toString().indexOf("@")+1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
*/
        ImageView downArrow1,downArrow2,downArrow3;
        LinearLayout ans1,ans2,ans3;

         downArrow1=dialogView.findViewById(R.id.downarrow1);
         downArrow2=dialogView.findViewById(R.id.downarrow2);
         downArrow3=dialogView.findViewById(R.id.downarrow3);

         ans1=dialogView.findViewById(R.id.linearAns1);
         ans2=dialogView.findViewById(R.id.linearAns2);
         ans3=dialogView.findViewById(R.id.linearAns3);

        downArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiExpandCheck1){
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_up);
                    ans1.setVisibility(View.VISIBLE);
                    wifiExpandCheck1=false;
                }else {
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_down);
                    ans1.setVisibility(View.GONE);
                    wifiExpandCheck1=true;
                }
            }
        });
        downArrow2.setOnClickListener(v1 ->{
            if (wifiExpandCheck2){
                downArrow2.setImageResource(R.drawable.ic_arrow_drop_up);
                ans2.setVisibility(View.VISIBLE);
                wifiExpandCheck2=false;
            }else {
                downArrow2.setImageResource(R.drawable.ic_arrow_drop_down);
                ans2.setVisibility(View.GONE);
                wifiExpandCheck2=true;
            }
        } );

        downArrow3.setOnClickListener(v1 ->{
            if (wifiExpandCheck3){
                downArrow3.setImageResource(R.drawable.ic_arrow_drop_up);
                ans3.setVisibility(View.VISIBLE);
                wifiExpandCheck3=false;
            }else {
                downArrow3.setImageResource(R.drawable.ic_arrow_drop_down);
                ans3.setVisibility(View.GONE);
                wifiExpandCheck3=true;
            }
        } );
        dialog.show();
    }

    @Override
    public void showNetworkAlert() {
        if(visibility){
            netWorkDialog.setMessage("Wifi is not connected");
            netWorkDialog.show();
        }
            wifiCheck=false;
            txtWifiName.setText("No WiFi Connection");
    }

    @Override
    public void dismissNetworkAlert(String wifiSSID) {
        netWorkDialog.dismiss();
        wifiCheck=true;
        wifiName=wifiSSID;
        txtWifiName.setText(wifiSSID.replace("\"","").trim());
    }

    @Override
    protected void onStart() {
        super.onStart();
        visibility=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visibility=false;
    }
}

interface CameraClickListener {
    void CameraClick(String info);
    void CameraDelete(String cameraInfo,int position);
}

 interface OnDataSetChangeListener {
    void onDataRemoved(int position);
    void onDataAdded();
}

interface NetworkChangeListener{
    void showNetworkAlert();
    void dismissNetworkAlert(String wifiSSID);
}

