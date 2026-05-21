package com.codexlive.youtube.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChannelUrlParserTest {

    private final ChannelUrlParser parser = new ChannelUrlParser();

    @Test
    @DisplayName("channelId URL에서 채널 식별자를 파싱한다")
    void parseChannelIdUrl() {
        // Given
        String url = "https://www.youtube.com/channel/UC_x5XG1OV2P6uZZ5FSM9Ttw";

        // When
        ChannelIdentifier identifier = parser.parse(url);

        // Then
        assertThat(identifier.type()).isEqualTo(ChannelIdentifier.Type.CHANNEL_ID);
        assertThat(identifier.value()).isEqualTo("UC_x5XG1OV2P6uZZ5FSM9Ttw");
    }

    @Test
    @DisplayName("@handle URL에서 핸들 식별자를 파싱한다")
    void parseHandleUrl() {
        // Given
        String url = "https://www.youtube.com/@GoogleDevelopers";

        // When
        ChannelIdentifier identifier = parser.parse(url);

        // Then
        assertThat(identifier.type()).isEqualTo(ChannelIdentifier.Type.HANDLE);
        assertThat(identifier.value()).isEqualTo("@GoogleDevelopers");
    }

    @Test
    @DisplayName("user URL에서 사용자명 식별자를 파싱한다")
    void parseUserUrl() {
        // Given
        String url = "https://www.youtube.com/user/GoogleDevelopers";

        // When
        ChannelIdentifier identifier = parser.parse(url);

        // Then
        assertThat(identifier.type()).isEqualTo(ChannelIdentifier.Type.USERNAME);
        assertThat(identifier.value()).isEqualTo("GoogleDevelopers");
    }

    @Test
    @DisplayName("앞뒤 공백은 제거한 뒤 URL을 파싱한다")
    void trimBeforeParsing() {
        // Given
        String url = "  https://www.youtube.com/@GoogleDevelopers  ";

        // When
        ChannelIdentifier identifier = parser.parse(url);

        // Then
        assertThat(identifier.type()).isEqualTo(ChannelIdentifier.Type.HANDLE);
        assertThat(identifier.value()).isEqualTo("@GoogleDevelopers");
    }

    @Test
    @DisplayName("빈 URL은 잘못된 채널 URL로 거부한다")
    void rejectBlankUrl() {
        // Given
        String url = " ";

        // When / Then
        assertThatThrownBy(() -> parser.parse(url))
            .isInstanceOf(InvalidChannelUrlException.class)
            .hasMessageContaining("지원하는 YouTube 채널 URL");
    }

    @Test
    @DisplayName("지원하지 않는 YouTube 경로는 잘못된 채널 URL로 거부한다")
    void rejectUnsupportedYoutubePath() {
        // Given
        String url = "https://www.youtube.com/watch?v=abc123";

        // When / Then
        assertThatThrownBy(() -> parser.parse(url))
            .isInstanceOf(InvalidChannelUrlException.class)
            .hasMessageContaining("지원하는 YouTube 채널 URL");
    }
}
