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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    List<UploadedFiles> videoList;
    OnItemClickListener onItemClickListener;

    public VideoAdapter(Context context, List<UploadedFiles> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(videoList.get(position).getFileUrl()).into(holder.videoThumbnail);
        holder.title.setText(videoList.get(position).getFileName());
        holder.size.setText(videoList.get(position).getFileSize());
        holder.date.setText(videoList.get(position).getFileUploadedDate());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(videoList.get(position)));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumbnail;
        TextView title;
        TextView size;
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);

            videoThumbnail = itemView.findViewById(R.id.file_img);
            title = itemView.findViewById(R.id.file_name);
            size = itemView.findViewById(R.id.file_size);
            date = itemView.findViewById(R.id.file_upload_date);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(UploadedFiles video);
    }
}
