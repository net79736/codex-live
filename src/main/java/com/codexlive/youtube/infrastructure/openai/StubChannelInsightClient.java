package com.codexlive.youtube.infrastructure.openai;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.port.ChannelInsightClient;

/**
 * OpenAI API 키 없이 로컬에서 UI·흐름을 확인할 때 사용한다.
 * {@code dev} 프로필에서만 활성화된다.
 */
@Component
@Profile("dev")
public class StubChannelInsightClient implements ChannelInsightClient {

    @Override
    public ChannelInsight generateInsight(ChannelSnapshot snapshot) {
        String title = snapshot.title() == null || snapshot.title().isBlank()
            ? "이 채널"
            : snapshot.title();

        return new ChannelInsight(
            """
                [데모 모드] OpenAI API 없이 생성된 샘플 요약입니다. \
                '%s' 채널(구독자 %,d, 영상 %,d개)을 기준으로 한 연습용 인사이트입니다. \
                실제 AI 분석을 쓰려면 OPENAI_API_KEY를 설정하고 dev 프로필을 끄세요.
                """.formatted(title, snapshot.subscriberCount(), snapshot.videoCount()).strip(),
            List.of(
                "채널 제목·설명이 검색·추천에 노출되는 핵심 자산입니다.",
                "공개 통계(구독자·조회수·영상 수)로 성장 추세를 가늠할 수 있습니다.",
                "데모 모드에서는 외부 AI 비용 없이 전체 흐름을 검증할 수 있습니다."
            ),
            List.of(
                "업로드 주기·썸네일·제목 패턴을 일관되게 맞추면 신규 시청자 유입에 도움이 됩니다.",
                "설명란·고정 댓글에 채널 정체성과 다음 영상 예고를 넣어 보세요.",
                "실서비스 분석은 OPENAI_API_KEY 설정 후 일반 모드로 전환하세요."
            ),
            List.of(
                "최근 10개 영상의 제목·썸네일 스타일을 표로 정리해 패턴을 찾아보세요.",
                "구독자 대비 조회수 비율이 낮은 영상 3개의 주제·길이를 비교해 보세요.",
                "다음 영상 1개는 시청자가 자주 묻는 주제로 기획해 보세요."
            )
        );
    }
}
