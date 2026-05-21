package com.codexlive.youtube.infrastructure.youtube;

import com.codexlive.youtube.domain.ChannelIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
class RestClientYoutubeDataApiGateway implements YoutubeDataApiGateway {

    private final RestClient restClient;

    RestClientYoutubeDataApiGateway(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .baseUrl("https://www.googleapis.com/youtube/v3")
            .build();
    }

    @Override
    public YoutubeChannelApiResponse fetchChannel(String apiKey, ChannelIdentifier identifier) {
        return restClient.get()
            .uri(uriBuilder -> applyIdentifier(
                uriBuilder.path("/channels")
                    .queryParam("part", "snippet,statistics")
                    .queryParam("key", apiKey),
                identifier
            ).build())
            .retrieve()
            .body(YoutubeChannelApiResponse.class);
    }

    private UriBuilder applyIdentifier(UriBuilder uriBuilder, ChannelIdentifier identifier) {
        return switch (identifier.type()) {
            case CHANNEL_ID -> uriBuilder.queryParam("id", identifier.value());
            case HANDLE -> uriBuilder.queryParam("forHandle", identifier.value().substring(1));
            case USERNAME -> uriBuilder.queryParam("forUsername", identifier.value());
        };
    }
}
