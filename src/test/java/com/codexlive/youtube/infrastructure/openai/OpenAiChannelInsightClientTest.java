package com.codexlive.youtube.infrastructure.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;

class OpenAiChannelInsightClientTest {

    private final ChannelSnapshot snapshot = new ChannelSnapshot(
        "UC_x5XG1OV2P6uZZ5FSM9Ttw",
        "Google for Developers",
        "Build with Google",
        "https://example.com/thumb.jpg",
        2_000_000L,
        100_000_000L,
        1_000L
    );

    @Test
    @DisplayName("OpenAI API 키가 없으면 OpenAI를 호출하지 않고 설정 오류를 던진다")
    void rejectMissingApiKey() {
        // Given
        RecordingOpenAiResponsesGateway gateway = new RecordingOpenAiResponsesGateway();
        OpenAiChannelInsightClient client = new OpenAiChannelInsightClient("", "gpt-5.2", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(MissingApiKeyException.class)
            .hasMessageContaining("OpenAI API 키");
        assertThat(gateway.called).isFalse();
    }

    @Test
    @DisplayName("OpenAI 모델 설정이 없으면 설정 오류를 던진다")
    void rejectMissingModel() {
        // Given
        RecordingOpenAiResponsesGateway gateway = new RecordingOpenAiResponsesGateway();
        OpenAiChannelInsightClient client = new OpenAiChannelInsightClient("openai-key", "", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(MissingApiKeyException.class)
            .hasMessageContaining("OpenAI 모델");
        assertThat(gateway.called).isFalse();
    }

    @Test
    @DisplayName("채널 정보를 OpenAI 요청으로 만들고 structured output을 domain 모델로 변환한다")
    void generateInsight() {
        // Given
        RecordingOpenAiResponsesGateway gateway = new RecordingOpenAiResponsesGateway();
        OpenAiChannelInsightClient client = new OpenAiChannelInsightClient("openai-key", "gpt-5.2", gateway);

        // When
        ChannelInsight insight = client.generateInsight(snapshot);

        // Then
        assertThat(gateway.apiKey).isEqualTo("openai-key");
        assertThat(gateway.request.model()).isEqualTo("gpt-5.2");
        assertThat(gateway.request.input()).contains("Google for Developers", "subscriberCount=2000000", "videoCount=1000");
        assertThat(insight.summary()).isEqualTo("개발자 대상 채널로 실용성이 강합니다.");
        assertThat(insight.strengths()).containsExactly("대상이 명확합니다.");
        assertThat(insight.opportunities()).containsExactly("입문자 경로를 강화할 수 있습니다.");
        assertThat(insight.nextActions()).hasSize(3);
    }

    @Test
    @DisplayName("OpenAI 호출 실패는 외부 API 오류로 변환한다")
    void wrapOpenAiFailure() {
        // Given
        RecordingOpenAiResponsesGateway gateway = new RecordingOpenAiResponsesGateway();
        gateway.failure = new RuntimeException("rate limit");
        OpenAiChannelInsightClient client = new OpenAiChannelInsightClient("openai-key", "gpt-5.2", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(ExternalApiException.class)
            .hasMessageContaining("OpenAI API 호출에 실패했습니다");
    }

    private static final class RecordingOpenAiResponsesGateway implements OpenAiResponsesGateway {
        private boolean called;
        private String apiKey;
        private OpenAiInsightRequest request;
        private RuntimeException failure;

        @Override
        public OpenAiInsightResponse generateInsight(String apiKey, OpenAiInsightRequest request) {
            called = true;
            this.apiKey = apiKey;
            this.request = request;
            if (failure != null) {
                throw failure;
            }
            return new OpenAiInsightResponse(
                "개발자 대상 채널로 실용성이 강합니다.",
                List.of("대상이 명확합니다."),
                List.of("입문자 경로를 강화할 수 있습니다."),
                List.of("채널 설명을 구체화하세요.", "초보자용 재생목록을 고정하세요.", "제목 형식을 통일하세요.")
            );
        }
    }
}