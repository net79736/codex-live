package com.codexlive.youtube.infrastructure.youtube;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelIdentifier;
import com.codexlive.youtube.domain.ChannelNotFoundException;
import com.codexlive.youtube.domain.ChannelSnapshot;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class YoutubeDataApiChannelClientTest {

    @Test
    @DisplayName("API 키가 없으면 YouTube를 호출하지 않고 설정 오류를 던진다")
    void rejectMissingApiKey() {
        // Given
        RecordingYoutubeDataApiGateway gateway = new RecordingYoutubeDataApiGateway();
        YoutubeDataApiChannelClient client = new YoutubeDataApiChannelClient("", gateway);

        // When / Then
        assertThatThrownBy(() -> client.fetchChannel(new ChannelIdentifier(ChannelIdentifier.Type.HANDLE, "@GoogleDevelopers")))
            .isInstanceOf(MissingApiKeyException.class)
            .hasMessageContaining("YouTube API 키");
        assertThat(gateway.called).isFalse();
    }

    @Test
    @DisplayName("YouTube 응답의 첫 번째 채널을 domain 모델로 변환한다")
    void mapYoutubeResponseToDomainSnapshot() {
        // Given
        RecordingYoutubeDataApiGateway gateway = new RecordingYoutubeDataApiGateway();
        gateway.response = YoutubeChannelApiResponse.of(List.of(new YoutubeChannelApiResponse.Item(
            "UC_x5XG1OV2P6uZZ5FSM9Ttw",
            new YoutubeChannelApiResponse.Snippet(
                "Google for Developers",
                "Build with Google",
                new YoutubeChannelApiResponse.Thumbnails(
                    new YoutubeChannelApiResponse.Thumbnail("https://example.com/default.jpg"),
                    new YoutubeChannelApiResponse.Thumbnail("https://example.com/medium.jpg"),
                    new YoutubeChannelApiResponse.Thumbnail("https://example.com/high.jpg")
                )
            ),
            new YoutubeChannelApiResponse.Statistics("2000000", "100000000", "1000")
        )));
        YoutubeDataApiChannelClient client = new YoutubeDataApiChannelClient("youtube-key", gateway);

        // When
        ChannelSnapshot snapshot = client.fetchChannel(new ChannelIdentifier(ChannelIdentifier.Type.CHANNEL_ID, "UC_x5XG1OV2P6uZZ5FSM9Ttw"));

        // Then
        assertThat(gateway.requestedKey).isEqualTo("youtube-key");
        assertThat(gateway.requestedIdentifier)
            .isEqualTo(new ChannelIdentifier(ChannelIdentifier.Type.CHANNEL_ID, "UC_x5XG1OV2P6uZZ5FSM9Ttw"));
        assertThat(snapshot.id()).isEqualTo("UC_x5XG1OV2P6uZZ5FSM9Ttw");
        assertThat(snapshot.title()).isEqualTo("Google for Developers");
        assertThat(snapshot.description()).isEqualTo("Build with Google");
        assertThat(snapshot.thumbnailUrl()).isEqualTo("https://example.com/high.jpg");
        assertThat(snapshot.subscriberCount()).isEqualTo(2_000_000L);
        assertThat(snapshot.viewCount()).isEqualTo(100_000_000L);
        assertThat(snapshot.videoCount()).isEqualTo(1_000L);
    }

    @Test
    @DisplayName("YouTube 응답에 채널이 없으면 채널 없음 오류를 던진다")
    void throwChannelNotFoundWhenResponseIsEmpty() {
        // Given
        RecordingYoutubeDataApiGateway gateway = new RecordingYoutubeDataApiGateway();
        gateway.response = YoutubeChannelApiResponse.of(List.of());
        YoutubeDataApiChannelClient client = new YoutubeDataApiChannelClient("youtube-key", gateway);

        // When / Then
        assertThatThrownBy(() -> client.fetchChannel(new ChannelIdentifier(ChannelIdentifier.Type.HANDLE, "@missing")))
            .isInstanceOf(ChannelNotFoundException.class)
            .hasMessageContaining("채널을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("YouTube 호출 실패는 외부 API 오류로 변환한다")
    void wrapYoutubeGatewayFailure() {
        // Given
        RecordingYoutubeDataApiGateway gateway = new RecordingYoutubeDataApiGateway();
        gateway.failure = new RuntimeException("timeout");
        YoutubeDataApiChannelClient client = new YoutubeDataApiChannelClient("youtube-key", gateway);

        // When / Then
        assertThatThrownBy(() -> client.fetchChannel(new ChannelIdentifier(ChannelIdentifier.Type.HANDLE, "@GoogleDevelopers")))
            .isInstanceOf(ExternalApiException.class)
            .hasMessageContaining("YouTube API 호출에 실패했습니다");
    }

    private static final class RecordingYoutubeDataApiGateway implements YoutubeDataApiGateway {
        private boolean called;
        private String requestedKey;
        private ChannelIdentifier requestedIdentifier;
        private YoutubeChannelApiResponse response = YoutubeChannelApiResponse.of(List.of());
        private RuntimeException failure;

        @Override
        public YoutubeChannelApiResponse fetchChannel(String apiKey, ChannelIdentifier identifier) {
            called = true;
            requestedKey = apiKey;
            requestedIdentifier = identifier;
            if (failure != null) {
                throw failure;
            }
            return response;
        }
    }
}
