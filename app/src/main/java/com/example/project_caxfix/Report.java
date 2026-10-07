package com.example.project_caxfix;

public final class Report {
    public final long id, authorUserId, createdAt;
    public final String authorName, neighborhood, status, title, category, description, imageUri;
    public final int supportCount, commentCount;
    public final boolean supported;

    public Report(long id, long authorUserId, String authorName, String neighborhood,
                  String status, String title, String category, String description,
                  String imageUri, long createdAt, int supportCount, int commentCount, boolean supported) {
        this.id = id; this.authorUserId = authorUserId; this.authorName = authorName;
        this.neighborhood = neighborhood; this.status = status; this.title = title;
        this.category = category; this.description = description; this.imageUri = imageUri;
        this.createdAt = createdAt; this.supportCount = supportCount;
        this.commentCount = commentCount; this.supported = supported;
    }
    public String getFormattedTimeAgo() {
        long minutes = Math.max(0, System.currentTimeMillis() - createdAt) / 60000;
        if (minutes < 1) return "Hace un momento";
        if (minutes < 60) return "Hace " + minutes + " min";
        if (minutes < 1440) return "Hace " + minutes / 60 + " h";
        return "Hace " + minutes / 1440 + " d";
    }
}
