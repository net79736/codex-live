package com.codexlive.youtube.infrastructure;

import com.codexlive.youtube.domain.ChannelSnapshot;

/**
 * "채널 인사이트"란 유튜브 채널의 공개 정보(채널명, 설명, 구독자수, 영상수 등)를 기반으로
 * 해당 채널의 특징, 강점, 개선 기회, 그리고 실질적으로 실행 가능한 다음 액션을 도출한 분석 결과를 의미합니다.
 * 프로덕트 관점에서는 "이 채널이 잘하고 있는 점은 무엇이고, 어떤 점을 개선하면 좋을지,
 * 운영자는 앞으로 무엇을 해야 하는지"에 대한 요약·추천·실천 방안을 한눈에 보여주는 지표입니다.
 * 
 * 이 클래스는 AI 모델(OpenAI, Ollama 등)에 전달할 프롬프트(질문/지시문)를 생성하여
 * 위와 같은 '채널 인사이트'를 한글로 구조화된(JSON) 형태로 반환하도록 유도합니다.
 */
public final class ChannelInsightPrompt {

    /**
     * 생성자
     */
    private ChannelInsightPrompt() {
    }

    /**
     /**
      * 채널의 공개 정보를 바탕으로 AI에게 분석 요청에 사용할 텍스트 프롬프트를 만든다.
      * 이 프롬프트는 유튜브 채널의 핵심 정보를 요약해서, OpenAI나 Ollama 같은 LLM 모델이
      * '채널 강점/기회/실행 방안' 등 실제 인사이트 추출을 할 수 있게 한다.
      * 
     * @param snapshot 채널 정보
     * @return 사용자 입력 프롬프트 (채널 ID, 채널 제목, 채널 설명, 구독자 수, 조회수, 영상 수)
     */
    public static String buildUserInput(ChannelSnapshot snapshot) {
        return """
            You are a YouTube channel strategy analyst. Return Korean insights for this public channel.
            Focus on actionable advice based only on the public fields below.
            Provide at least three nextActions. Each next action must start with a concrete verb.

            channelId=%s
            title=%s
            description=%s
            subscriberCount=%d
            viewCount=%d
            videoCount=%d
            """.formatted(
            snapshot.id(),
            snapshot.title(),
            snapshot.description(),
            snapshot.subscriberCount(),
            snapshot.viewCount(),
            snapshot.videoCount()
        );
    }

    /**
     * 시스템 입력 프롬프트를 생성한다. (JSON 형식 지시문)
     * @return 시스템 입력 프롬프트
     */
    public static String systemJsonInstruction() {
        return """
            Respond with JSON only. Use this exact shape:
            {"summary":"string","strengths":["string"],"opportunities":["string"],"nextActions":["string"]}
            summary must be non-empty Korean text.
            strengths, opportunities, and nextActions must each have at least one item.
            nextActions must have at least three items. Each next action must start with a concrete verb.
            """;
    }
}
