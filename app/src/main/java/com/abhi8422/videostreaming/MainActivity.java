package com.abhi8422.videostreaming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.ImageButton;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String rtspUrl="";
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    VLCVideoLayout videoLayout;
    AlertDialog dialog;
    ImageButton btnClose;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClose=findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            onBackPressed();
        });
        rtspUrl=getIntent().getStringExtra("URL");
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        libVLC=new LibVLC(this,args);
        mediaPlayer =new MediaPlayer(libVLC);
        videoLayout=findViewById(R.id.videoLayout);
        file = new File(Environment.getExternalStorageDirectory()+"/sample.mp4");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.attachViews(videoLayout,null,false,false);
        Media media=new Media(libVLC, Uri.parse(rtspUrl));
        media.setHWDecoderEnabled(true,false);
        media.addOption(":network-caching=500");
        media.addOption(":sout=#duplicate{dst=file{dst=" + file.getAbsolutePath() + "},dst=display}");
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
