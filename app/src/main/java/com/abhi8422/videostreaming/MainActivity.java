package com.abhi8422.videostreaming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
    boolean streamCheck1=true;
    AlertDialog dialog;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.help:
                showStreamHelpAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showStreamHelpAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.stream_help_alert_layout, null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();

        ImageView downArrow1;
        TextView ans1;

        downArrow1=dialogView.findViewById(R.id.downarrow1);
        ans1=dialogView.findViewById(R.id.txtStreamAns1);

        downArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (streamCheck1){
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_up);
                    ans1.setVisibility(View.VISIBLE);
                    streamCheck1=false;
                }else {
                    downArrow1.setImageResource(R.drawable.ic_arrow_drop_down);
                    ans1.setVisibility(View.GONE);
                    streamCheck1=true;
                }
            }
        });
        dialog.show();
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










































