package com.abhi8422.videostreaming;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CameraAdapter  extends RecyclerView.Adapter<CameraAdapter.CameraViewHolder> implements OnDataSetChangeListener{
    List<String> cameraNames;
    CameraClickListener listener;

    public CameraAdapter(List<String> cameraNames, CameraClickListener listener) {
        this.cameraNames = cameraNames;
        this.listener = listener;
    }

    @Override
    public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera_shortcut,parent,false);
        return new CameraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraAdapter.CameraViewHolder holder, int position) {
        String[] cameraInfo=cameraNames.get(position).split("WN");
            holder.name.setText(cameraInfo[1]);
            holder.url.setText(cameraInfo[0]);
    }


    @Override
    public int getItemCount() {
        return cameraNames.size();
    }

    @Override
    public void onDataRemoved(int position) {
        cameraNames.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public void onDataAdded() {
        this.notifyDataSetChanged();
    }

    class CameraViewHolder extends RecyclerView.ViewHolder{
    TextView name,url;
    ImageView imgDel;
        public CameraViewHolder(@NonNull  View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.txtCamera);
            url=itemView.findViewById(R.id.txtCameraURL);
            imgDel=itemView.findViewById(R.id.imgDel);
            itemView.setOnClickListener(v -> {

                listener.CameraClick(cameraNames.get(getAdapterPosition()));
            });
            imgDel.setOnClickListener(v -> {
                listener.CameraDelete(cameraNames.get(getAdapterPosition()),getAdapterPosition());
                Toast.makeText(itemView.getContext(), "Camera shortcut is deleted", Toast.LENGTH_SHORT).show();
                onDataRemoved(getAdapterPosition());
            });
        }
    }
}
