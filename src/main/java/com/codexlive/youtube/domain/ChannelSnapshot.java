package com.codexlive.youtube.domain;

import java.util.Objects;

/**
 * 채널 정보를 저장하는 레코드
 */
public record ChannelSnapshot(
    String id, // 채널 ID
    String title, // 채널 제목
    String description, // 채널 설명
    String thumbnailUrl, // 채널 썸네일
    long subscriberCount, // 구독자 수
    long viewCount, // 조회수
    long videoCount // 영상 수
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
