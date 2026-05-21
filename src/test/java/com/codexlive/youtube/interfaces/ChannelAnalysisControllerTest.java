package com.codexlive.youtube.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codexlive.youtube.application.AnalyzeChannelUseCase;
import com.codexlive.youtube.application.ChannelAnalysisResult;
import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelNotFoundException;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.InvalidChannelUrlException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelAnalysisController.class)
class ChannelAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyzeChannelUseCase analyzeChannelUseCase;

    @Test
    @DisplayName("POST /api/channel-analyses는 분석 결과를 JSON으로 반환한다")
    void createChannelAnalysis() throws Exception {
        // Given
        when(analyzeChannelUseCase.analyze(any())).thenReturn(new ChannelAnalysisResult(
            new ChannelSnapshot(
                "UC_x5XG1OV2P6uZZ5FSM9Ttw",
                "Google for Developers",
                "Build with Google",
                "https://example.com/thumb.jpg",
                2_000_000L,
                100_000_000L,
                1_000L
            ),
            new ChannelInsight(
                "개발자를 위한 실용 콘텐츠가 누적된 채널입니다.",
                List.of("명확한 대상 독자"),
                List.of("입문자용 경로를 더 분명히 안내할 수 있습니다."),
                List.of("채널 설명에 입문자용 재생목록을 추가하세요.", "최근 영상 제목의 주제를 통일하세요.", "주간 업로드 리듬을 명시하세요.")
            )
        ));

        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":"https://www.youtube.com/@GoogleDevelopers"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.channel.title").value("Google for Developers"))
            .andExpect(jsonPath("$.channel.subscriberCount").value(2_000_000))
            .andExpect(jsonPath("$.insight.summary").value("개발자를 위한 실용 콘텐츠가 누적된 채널입니다."))
            .andExpect(jsonPath("$.insight.nextActions[2]").value("주간 업로드 리듬을 명시하세요."));
        verify(analyzeChannelUseCase).analyze(any());
    }

    @Test
    @DisplayName("빈 URL 요청은 400 Bad Request로 응답하고 use case를 호출하지 않는다")
    void rejectBlankUrlRequest() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":" "}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("채널 URL을 입력하세요."));
        verifyNoInteractions(analyzeChannelUseCase);
    }

    @Test
    @DisplayName("잘못된 URL은 400 Bad Request로 응답한다")
    void mapInvalidUrlToBadRequest() throws Exception {
        // Given
        when(analyzeChannelUseCase.analyze(any()))
            .thenThrow(new InvalidChannelUrlException("지원하는 YouTube 채널 URL을 입력하세요."));

        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":"https://www.youtube.com/watch?v=abc123"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("지원하는 YouTube 채널 URL을 입력하세요."));
    }

    @Test
    @DisplayName("채널 없음은 400 Bad Request로 응답한다")
    void mapChannelNotFoundToBadRequest() throws Exception {
        // Given
        when(analyzeChannelUseCase.analyze(any()))
            .thenThrow(new ChannelNotFoundException("채널을 찾을 수 없습니다."));

        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":"https://www.youtube.com/@missing"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("채널을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("API 키 누락은 503 Service Unavailable로 응답한다")
    void mapMissingApiKeyToServiceUnavailable() throws Exception {
        // Given
        when(analyzeChannelUseCase.analyze(any()))
            .thenThrow(new MissingApiKeyException("서비스 설정에 필요한 API 키가 없습니다."));

        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":"https://www.youtube.com/@GoogleDevelopers"}
                    """))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.message").value("서비스 설정에 필요한 API 키가 없습니다."));
    }

    @Test
    @DisplayName("외부 API 실패는 502 Bad Gateway로 응답한다")
    void mapExternalApiFailureToBadGateway() throws Exception {
        // Given
        when(analyzeChannelUseCase.analyze(any()))
            .thenThrow(new ExternalApiException("외부 API 호출에 실패했습니다."));

        // When / Then
        mockMvc.perform(post("/api/channel-analyses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"channelUrl":"https://www.youtube.com/@GoogleDevelopers"}
                    """))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.message").value("외부 API 호출에 실패했습니다."));
    }
}
