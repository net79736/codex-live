package com.codexlive.youtube.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codexlive.youtube.domain.ChannelIdentifier;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.InvalidChannelUrlException;
import com.codexlive.youtube.domain.port.ChannelInsightClient;
import com.codexlive.youtube.domain.port.YoutubeChannelClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnalyzeChannelUseCaseTest {

    @Test
    @DisplayName("채널 URL을 파싱하고 YouTube 조회 후 인사이트를 생성한다")
    void analyzeChannel() {
        // Given
        RecordingYoutubeClient youtubeClient = new RecordingYoutubeClient();
        RecordingInsightClient insightClient = new RecordingInsightClient();
        AnalyzeChannelUseCase useCase = new AnalyzeChannelUseCase(youtubeClient, insightClient);

        // When
        ChannelAnalysisResult result = useCase.analyze(
            new AnalyzeChannelCommand("https://www.youtube.com/@GoogleDevelopers")
        );

        // Then
        assertThat(youtubeClient.requestedIdentifier)
            .isEqualTo(new ChannelIdentifier(ChannelIdentifier.Type.HANDLE, "@GoogleDevelopers"));
        assertThat(insightClient.requestedSnapshot).isEqualTo(youtubeClient.snapshot);
        assertThat(youtubeClient.events).containsExactly("youtube.fetch");
        assertThat(insightClient.events).containsExactly("insight.generate");
        assertThat(result.channel()).isEqualTo(youtubeClient.snapshot);
        assertThat(result.insight()).isEqualTo(insightClient.insight);
    }

    @Test
    @DisplayName("잘못된 URL이면 외부 포트를 호출하지 않는다")
    void rejectInvalidUrlBeforeCallingPorts() {
        // Given
        RecordingYoutubeClient youtubeClient = new RecordingYoutubeClient();
        RecordingInsightClient insightClient = new RecordingInsightClient();
        AnalyzeChannelUseCase useCase = new AnalyzeChannelUseCase(youtubeClient, insightClient);

        // When / Then
        assertThatThrownBy(() -> useCase.analyze(new AnalyzeChannelCommand("https://www.youtube.com/watch?v=abc123")))
            .isInstanceOf(InvalidChannelUrlException.class);
        assertThat(youtubeClient.requestedIdentifier).isNull();
        assertThat(insightClient.requestedSnapshot).isNull();
    }

    private static final class RecordingYoutubeClient implements YoutubeChannelClient {
        private final List<String> events = new ArrayList<>();
        private final ChannelSnapshot snapshot = new ChannelSnapshot(
            "UC_x5XG1OV2P6uZZ5FSM9Ttw",
            "Google for Developers",
            "Build with Google",
            "https://example.com/thumb.jpg",
            2_000_000L,
            100_000_000L,
            1_000L
        );
        private ChannelIdentifier requestedIdentifier;

        @Override
        public ChannelSnapshot fetchChannel(ChannelIdentifier identifier) {
            events.add("youtube.fetch");
            requestedIdentifier = identifier;
            return snapshot;
        }
    }

    private static final class RecordingInsightClient implements ChannelInsightClient {
        private final List<String> events = new ArrayList<>();
        private final ChannelInsight insight = new ChannelInsight(
            "개발자를 위한 실용 콘텐츠가 누적된 채널입니다.",
            List.of("명확한 대상 독자", "높은 콘텐츠 누적량"),
            List.of("입문자용 경로를 더 분명히 안내할 수 있습니다."),
            List.of("채널 설명에 입문자용 재생목록을 추가하세요.", "최근 영상 제목의 주제를 통일하세요.", "주간 업로드 리듬을 명시하세요.")
        );
        private ChannelSnapshot requestedSnapshot;

        @Override
        public ChannelInsight generateInsight(ChannelSnapshot snapshot) {
            events.add("insight.generate");
            requestedSnapshot = snapshot;
            return insight;
        }
    }
}
