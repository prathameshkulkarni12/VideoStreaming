package com.abhi8422.videostreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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










































