package com.codexlive.youtube.infrastructure.ollama;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;

class OllamaChannelInsightClientTest {

    /**
     * 테스트 데이터
     */
    private final ChannelSnapshot snapshot = new ChannelSnapshot(
        "UC_x5XG1OV2P6uZZ5FSM9Ttw", // 채널 ID
        "Google for Developers", // 채널 제목
        "Build with Google", // 채널 설명
        "https://example.com/thumb.jpg", // 채널 썸네일
        2_000_000L, // 구독자 수
        100_000_000L, // 조회수
        1_000L // 영상 수
    );

    @Test
    @DisplayName("Ollama 모델 설정이 없으면 Ollama를 호출하지 않고 설정 오류를 던진다")
    void rejectMissingModel() {
        // Given
        RecordingOllamaChatGateway gateway = new RecordingOllamaChatGateway();
        OllamaChannelInsightClient client = new OllamaChannelInsightClient("", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(MissingApiKeyException.class)
            .hasMessageContaining("Ollama 모델");
        assertThat(gateway.called).isFalse();
    }

    @Test
    @DisplayName("채널 정보를 Ollama 요청으로 만들고 JSON 응답을 domain 모델로 변환한다")
    void generateInsight() {
        // Given
        RecordingOllamaChatGateway gateway = new RecordingOllamaChatGateway();
        OllamaChannelInsightClient client = new OllamaChannelInsightClient("qwen2.5:14b", gateway);

        // When
        ChannelInsight insight = client.generateInsight(snapshot);

        // Then
        assertThat(gateway.request.model()).isEqualTo("qwen2.5:14b");
        assertThat(gateway.request.input()).contains("Google for Developers", "subscriberCount=2000000", "videoCount=1000");
        assertThat(insight.summary()).isEqualTo("개발자 대상 채널로 실용성이 강합니다.");
        assertThat(insight.strengths()).containsExactly("대상이 명확합니다.");
        assertThat(insight.opportunities()).containsExactly("입문자 경로를 강화할 수 있습니다.");
        assertThat(insight.nextActions()).hasSize(3);
    }

    @Test
    @DisplayName("Ollama 호출 실패는 외부 API 오류로 변환한다")
    void wrapOllamaFailure() {
        // Given
        RecordingOllamaChatGateway gateway = new RecordingOllamaChatGateway();
        gateway.failure = new RuntimeException("connection refused");
        OllamaChannelInsightClient client = new OllamaChannelInsightClient("qwen2.5:14b", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(ExternalApiException.class)
            .hasMessageContaining("Ollama API 호출에 실패했습니다");
    }

    @Test
    @DisplayName("Ollama 응답 파싱 실패는 외부 API 오류로 변환한다")
    void wrapInvalidResponse() {
        // Given
        RecordingOllamaChatGateway gateway = new RecordingOllamaChatGateway();
        gateway.failure = new IllegalStateException("invalid JSON");
        OllamaChannelInsightClient client = new OllamaChannelInsightClient("qwen2.5:14b", gateway);

        // When / Then
        assertThatThrownBy(() -> client.generateInsight(snapshot))
            .isInstanceOf(ExternalApiException.class)
            .hasMessageContaining("Ollama API 호출에 실패했습니다");
    }

    private static final class RecordingOllamaChatGateway implements OllamaChatGateway {
        private boolean called;
        private OllamaInsightRequest request;
        private RuntimeException failure;

        @Override
        public OllamaInsightResponse generateInsight(OllamaInsightRequest request) {
            called = true;
            this.request = request;
            if (failure != null) {
                throw failure;
            }
            return new OllamaInsightResponse(
                "개발자 대상 채널로 실용성이 강합니다.",
                List.of("대상이 명확합니다."),
                List.of("입문자 경로를 강화할 수 있습니다."),
                List.of("채널 설명을 구체화하세요.", "초보자용 재생목록을 고정하세요.", "제목 형식을 통일하세요.")
            );
        }
    }
}
