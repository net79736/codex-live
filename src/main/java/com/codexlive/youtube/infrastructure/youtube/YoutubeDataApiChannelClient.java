package com.codexlive.youtube.infrastructure.youtube;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelIdentifier;
import com.codexlive.youtube.domain.ChannelNotFoundException;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.port.YoutubeChannelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class YoutubeDataApiChannelClient implements YoutubeChannelClient {

    private final String apiKey;
    private final YoutubeDataApiGateway gateway;

    public YoutubeDataApiChannelClient(
        @Value("${youtube.api-key:}") String apiKey,
        YoutubeDataApiGateway gateway
    ) {
        this.apiKey = apiKey;
        this.gateway = gateway;
    }

    @Override
    public ChannelSnapshot fetchChannel(ChannelIdentifier identifier) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingApiKeyException("서비스 설정에 필요한 YouTube API 키가 없습니다.");
        }

        YoutubeChannelApiResponse response;
        try {
            response = gateway.fetchChannel(apiKey, identifier);
        } catch (RuntimeException exception) {
            throw new ExternalApiException("YouTube API 호출에 실패했습니다.", exception);
        }

        YoutubeChannelApiResponse.Item item = firstItem(response);
        return new ChannelSnapshot(
            item.id(),
            item.snippet().title(),
            item.snippet().description(),
            thumbnailUrl(item.snippet().thumbnails()),
            parseMetric(item.statistics().subscriberCount()),
            parseMetric(item.statistics().viewCount()),
            parseMetric(item.statistics().videoCount())
        );
    }

    private YoutubeChannelApiResponse.Item firstItem(YoutubeChannelApiResponse response) {
        if (response == null || response.items() == null || response.items().isEmpty()) {
            throw new ChannelNotFoundException("채널을 찾을 수 없습니다.");
        }
        return response.items().get(0);
    }

    private String thumbnailUrl(YoutubeChannelApiResponse.Thumbnails thumbnails) {
        if (thumbnails == null) {
            return "";
        }
        if (thumbnails.high() != null && thumbnails.high().url() != null) {
            return thumbnails.high().url();
        }
        if (thumbnails.medium() != null && thumbnails.medium().url() != null) {
            return thumbnails.medium().url();
        }
        if (thumbnails.defaultThumbnail() != null && thumbnails.defaultThumbnail().url() != null) {
            return thumbnails.defaultThumbnail().url();
        }
        return "";
    }

    private long parseMetric(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        return Long.parseLong(value);
    }
}
