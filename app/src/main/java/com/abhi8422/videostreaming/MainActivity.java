package com.abhi8422.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.ILibVLC;
import org.videolan.libvlc.util.VLCVideoLayout;

public class MainActivity extends AppCompatActivity {

    private String rtspUrl="";
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    VLCVideoLayout videoLayout;
    AlertDialog dialog;
    ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClose=findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            onBackPressed();
        });
        rtspUrl=getIntent().getStringExtra("URL");
        libVLC=new LibVLC(this);
        mediaPlayer =new MediaPlayer(libVLC);
        videoLayout=findViewById(R.id.videoLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.attachViews(videoLayout,null,false,false);
        Media media=new Media(libVLC, Uri.parse(rtspUrl));
        media.setHWDecoderEnabled(true,false);
        media.addOption(":network-caching=500");
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this,MainActivity2.class));
        finish();
    }

    public void close(MenuItem item) {
        dialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.detachViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        libVLC.release();
    }

}



