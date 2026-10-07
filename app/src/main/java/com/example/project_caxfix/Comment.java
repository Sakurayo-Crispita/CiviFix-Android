package com.example.project_caxfix;
public final class Comment {
    public final String authorName, body;
    public final long createdAt;
    public Comment(String authorName, String body, long createdAt) {
        this.authorName = authorName; this.body = body; this.createdAt = createdAt;
    }
}
