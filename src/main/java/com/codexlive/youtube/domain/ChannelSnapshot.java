package com.codexlive.youtube.domain;

import java.util.Objects;

public record ChannelSnapshot(
    String id,
    String title,
    String description,
    String thumbnailUrl,
    long subscriberCount,
    long viewCount,
    long videoCount
) {

    public ChannelSnapshot {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        description = Objects.requireNonNullElse(description, "");
        thumbnailUrl = Objects.requireNonNullElse(thumbnailUrl, "");
        if (subscriberCount < 0 || viewCount < 0 || videoCount < 0) {
            throw new IllegalArgumentException("channel metrics must not be negative");
        }
    }
}
