package com.codexlive.youtube.domain;

import java.net.URI;

public class ChannelUrlParser {

    private static final String INVALID_MESSAGE =
        "지원하는 YouTube 채널 URL을 입력하세요. 예: https://www.youtube.com/@channelname";

    public ChannelIdentifier parse(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new InvalidChannelUrlException(INVALID_MESSAGE);
        }

        URI uri = parseUri(rawUrl.trim());
        if (!isYoutubeHost(uri.getHost())) {
            throw new InvalidChannelUrlException(INVALID_MESSAGE);
        }

        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            throw new InvalidChannelUrlException(INVALID_MESSAGE);
        }

        String[] segments = path.substring(1).split("/");
        if (segments.length == 0) {
            throw new InvalidChannelUrlException(INVALID_MESSAGE);
        }

        if (segments[0].startsWith("@") && segments[0].length() > 1) {
            return new ChannelIdentifier(ChannelIdentifier.Type.HANDLE, segments[0]);
        }

        if (segments.length >= 2 && "channel".equals(segments[0]) && !segments[1].isBlank()) {
            return new ChannelIdentifier(ChannelIdentifier.Type.CHANNEL_ID, segments[1]);
        }

        if (segments.length >= 2 && "user".equals(segments[0]) && !segments[1].isBlank()) {
            return new ChannelIdentifier(ChannelIdentifier.Type.USERNAME, segments[1]);
        }

        throw new InvalidChannelUrlException(INVALID_MESSAGE);
    }

    private URI parseUri(String url) {
        try {
            return URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new InvalidChannelUrlException(INVALID_MESSAGE);
        }
    }

    private boolean isYoutubeHost(String host) {
        return "youtube.com".equalsIgnoreCase(host) || "www.youtube.com".equalsIgnoreCase(host);
    }
}
