package com.example.storix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    Context context;
    List<UploadedFiles> audioList;
    OnItemClickListener onItemClickListener;

    public AudioAdapter(Context context, List<UploadedFiles> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new AudioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(audioList.get(position).getFileUrl()).into(holder.audioThumbnail);
        holder.title.setText(audioList.get(position).getFileName());
        holder.size.setText(audioList.get(position).getFileSize());
        holder.date.setText(audioList.get(position).getFileUploadedDate());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(audioList.get(position)));
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView audioThumbnail;
        TextView title;
        TextView size;
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);

            audioThumbnail = itemView.findViewById(R.id.file_img);
            title = itemView.findViewById(R.id.file_name);
            size = itemView.findViewById(R.id.file_size);
            date = itemView.findViewById(R.id.file_upload_date);
        }
    }

    public void setOnItemClickListener(AudioAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(UploadedFiles video);
    }

}
