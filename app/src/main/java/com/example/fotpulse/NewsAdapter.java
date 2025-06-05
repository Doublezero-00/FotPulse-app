package com.example.fotpulse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private OnNewsItemClickListener listener;

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;

        this.listener = null;
    }

    public NewsAdapter(List<NewsItem> newsList, OnNewsItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    public void updateData(List<NewsItem> newNewsList) {
        this.newsList.clear();
        this.newsList.addAll(newNewsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem currentItem = newsList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.contentTextView.setText(currentItem.getContent());

        String imageUrl = currentItem.getImageUrl();
        Log.d("NewsAdapter", "Loading image for: " + currentItem.getTitle() + " URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder))
                    .into(holder.imageView);
        } else {

            Glide.with(holder.itemView.getContext()).clear(holder.imageView);
            holder.imageView.setImageResource(R.drawable.placeholder);
            Log.w("NewsAdapter", "Empty or null image URL for news item: " + currentItem.getTitle());
        }

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> {
                listener.onNewsItemClick(currentItem);
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        public ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleText);
            contentTextView = itemView.findViewById(R.id.contentText);
            imageView = itemView.findViewById(R.id.imageView);

            if (imageView == null) {
                Log.e("NewsViewHolder", "ImageView with ID R.id.imageView not found in item_news.xml!");
            }
        }
    }
}