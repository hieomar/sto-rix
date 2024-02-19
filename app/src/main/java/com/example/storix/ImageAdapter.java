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

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    List<UploadedFiles> imgList;
    OnItemClickListener onItemClickListener;

    public ImageAdapter(Context context, List<UploadedFiles> imgList) {
        this.context = context;
        this.imgList = imgList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.img_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(imgList.get(position).getFileUrl()).into(holder.imgThumbnail);
        holder.title.setText(imgList.get(position).getFileName());
        holder.date.setText(imgList.get(position).getFileUploadedDate());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(imgList.get(position)));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumbnail;
        TextView title;
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.img_list_image);
            title = itemView.findViewById(R.id.img_list_item_name);
            date = itemView.findViewById(R.id.img_list_item_date);
        }
    }

    public void setOnItemClickListener(ImageAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(UploadedFiles video);
    }
}
