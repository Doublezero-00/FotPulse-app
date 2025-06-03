package com.example.fotpulse;

public class NewsItem {
    private int id;
    private String category;
    private String title;
    private String content;
    private String imageUrl; // Optional, if your news has images

    public NewsItem(int id, String category, String title, String content, String imageUrl) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    // Add getters for all fields
    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }

    // You can also add setters if needed, but often not for immutable data from DB
}