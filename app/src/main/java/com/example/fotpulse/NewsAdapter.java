package com.example.fotpulse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView imageView;

        public NewsViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.titleText);
            content = v.findViewById(R.id.contentText);
            imageView = v.findViewById(R.id.imageView);
        }
    }

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());

        Glide.with(holder.imageView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
