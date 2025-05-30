package com.example.fotpulse;

public class NewsItem {
    private int id;
    private String title;
    private String content;
    private String category;
    private String media_url;

    public NewsItem(int id, String title, String content, String category,  String media_url) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.media_url = media_url;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getImageUrl() { return media_url; }
}
