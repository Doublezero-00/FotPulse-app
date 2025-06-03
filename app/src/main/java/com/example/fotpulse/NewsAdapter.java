package com.example.fotpulse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    // NEW: Method to update data
    public void updateData(List<NewsItem> newNewsList) {
        this.newsList.clear();
        this.newsList.addAll(newNewsList);
        notifyDataSetChanged(); // Notify RecyclerView that data has changed
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false); // Ensure this matches your item layout name
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem currentItem = newsList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.contentTextView.setText(currentItem.getContent());
        // If you have image loading, implement it here (e.g., using Glide/Picasso)
        // holder.imageView.setImageResource(R.drawable.placeholder); // Placeholder if no image library
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        // public ImageView imageView; // Uncomment if you add an ImageView in item_news.xml

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleText); // Ensure this ID matches item_news.xml
            contentTextView = itemView.findViewById(R.id.contentText); // Ensure this ID matches item_news.xml
            // imageView = itemView.findViewById(R.id.imageView); // Uncomment if you add an ImageView in item_news.xml
        }
    }
}