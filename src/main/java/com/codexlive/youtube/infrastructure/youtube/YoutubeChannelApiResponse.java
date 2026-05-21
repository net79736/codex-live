package com.codexlive.youtube.infrastructure.youtube;

import java.util.List;

record YoutubeChannelApiResponse(List<Item> items) {

    static YoutubeChannelApiResponse of(List<Item> items) {
        return new YoutubeChannelApiResponse(items == null ? List.of() : List.copyOf(items));
    }

    record Item(String id, Snippet snippet, Statistics statistics) {
    }

    record Snippet(String title, String description, Thumbnails thumbnails) {
    }

    record Thumbnails(Thumbnail defaultThumbnail, Thumbnail medium, Thumbnail high) {
    }

    record Thumbnail(String url) {
    }

    record Statistics(String subscriberCount, String viewCount, String videoCount) {
    }
}
