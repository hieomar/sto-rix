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

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    Context context;
    List<UploadedFiles> documentList;
    OnItemClickListener onItemClickListener;

    public DocumentAdapter(Context context, List<UploadedFiles> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new DocumentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(documentList.get(position).getFileUrl()).into(holder.documentThumbnail);
        holder.title.setText(documentList.get(position).getFileName());
        holder.size.setText(documentList.get(position).getFileSize());
        holder.date.setText(documentList.get(position).getFileUploadedDate());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(documentList.get(position)));
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView documentThumbnail;
        TextView title;
        TextView size;
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);

            documentThumbnail = itemView.findViewById(R.id.file_img);
            title = itemView.findViewById(R.id.file_name);
            size = itemView.findViewById(R.id.file_size);
            date = itemView.findViewById(R.id.file_upload_date);
        }
    }

    public void setOnItemClickListener(DocumentAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(UploadedFiles video);
    }

}
