package com.abhi8422.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
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
    boolean wifiCheck,expandCheck1=true,expandCheck2=true,expandCheck3=true,
            expandCheck4=true,expandCheck5=true,expandCheck6=true,visibility=true;
    CameraAdapter adapter;
    AlertDialog dialog,netWorkDialog;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;

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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, 101);
        cameraRecycler =findViewById(R.id.recyclerView);
        id=findViewById(R.id.edtId);
        btnView =findViewById(R.id.button2);
        btnShortcutView=findViewById(R.id.btnShortcut);
        textInputLayout=findViewById(R.id.textField);
        txtWifiName=findViewById(R.id.txtwifiname);
        btnWifi=findViewById(R.id.btnWifi);
        drawerLayout=findViewById(R.id.drawerLayout);

        configureToolbar();
        configureNavDrawer();

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

    private void configureToolbar() {
        Toggle = new ActionBarDrawerToggle(this,
                drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(Toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toggle.syncState();
    }

    private void configureNavDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
             int menuId = item.getItemId();
                switch (menuId) {
                    case R.id.wifi:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        break;
                    case R.id.help:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        showHelpAlert();
                        break;
                    case R.id.exit:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        finish();
                        break;
                }
                return true;
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
        return strings;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.help:
                showHelpAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelpAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help_infoalert_layout, null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();
        ScrollView sv=dialogView.findViewById(R.id.scrollView);
        sv.scrollTo(0,300);

        TextView text1=dialogView.findViewById(R.id.txtQuestion);
        TextView text2=dialogView.findViewById(R.id.txtQuestion2);
        TextView text3=dialogView.findViewById(R.id.txtQuestion5);
        TextView text4=dialogView.findViewById(R.id.txtQuestion3);
        TextView text5=dialogView.findViewById(R.id.txtQuestion4);
        TextView text6=dialogView.findViewById(R.id.txtQuestion6);

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck1){
                    text1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                    dialogView.findViewById(R.id.linear).setVisibility(View.VISIBLE);
                    expandCheck1=false;
                }else {
                    text1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                    dialogView.findViewById(R.id.linear).setVisibility(View.GONE);
                    expandCheck1=true;
                }
            }
        });
        text2.setOnClickListener(v1 ->{
            if (expandCheck2){
                text2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                dialogView.findViewById(R.id.linear2).setVisibility(View.VISIBLE);
                expandCheck2=false;
            }else {
                text2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                dialogView.findViewById(R.id.linear2).setVisibility(View.GONE);
                expandCheck2=true;
            }
        } );
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck5){
                    text3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                    dialogView.findViewById(R.id.linearAns3).setVisibility(View.VISIBLE);
                    expandCheck5=false;
                }else {
                    text3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                    dialogView.findViewById(R.id.linearAns3).setVisibility(View.GONE);
                    expandCheck5=true;
                }
            }
        });
        text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck3){
                    text4.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                    dialogView.findViewById(R.id.linearAns1).setVisibility(View.VISIBLE);
                    expandCheck3=false;
                }else {
                    text4.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                    dialogView.findViewById(R.id.linearAns1).setVisibility(View.GONE);
                    expandCheck3=true;
                }
            }
        });
        text5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck4){
                    text5.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                    dialogView.findViewById(R.id.linearAns2).setVisibility(View.VISIBLE);
                    expandCheck4=false;
                }else {
                    text5.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                    dialogView.findViewById(R.id.linearAns2).setVisibility(View.GONE);
                    expandCheck4=true;
                }
            }
        });
        text6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandCheck6){
                    text6.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                    dialogView.findViewById(R.id.txtStreamAns1).setVisibility(View.VISIBLE);
                    expandCheck6=false;
                }else {
                    text6.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                    dialogView.findViewById(R.id.txtStreamAns1).setVisibility(View.GONE);
                    expandCheck6=true;
                }
            }
        });

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

