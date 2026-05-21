package com.codexlive.youtube.infrastructure.ollama;

import java.util.List;

/**
 * OllamaInsightResponse는 Ollama LLM(로컬 AI) API로부터 받은 채널 인사이트 분석 결과를 담는 immutable 데이터 구조입니다.
 *
 * 이 레코드는 YouTube 채널의 공개 정보를 분석하여 추출된 네 가지 핵심 인사이트 항목을 캡슐화합니다:
 *   - summary:      AI가 한글로 요약한 채널의 핵심 특성 및 진단(비어 있으면 안 됨)
 *   - strengths:    채널의 강점 목록(1개 이상, ex. 콘텐츠 차별성, 구독자 반응성 등)
 *   - opportunities: 개선 기회 또는 향후 성장 전략 목록(1개 이상, 문제점 또는 성장 포인트 제안)
 *   - nextActions:  AI가 실제로 실행 가능한 다음 행동 제안 리스트(3개 이상, 반드시 구체적 동사로 시작)
 *
 * 이 구조는 Ollama API가 반환하는 JSON의 구조와 1:1로 매핑되며,
 * application 및 interface 계층에서 인사이트 분석 결과의 이동/표현에 사용됩니다.
 *
 * 예시(JSON):
 * {
 *   "summary": "채널은 ___ 분야에서 고유한 강점이 있으나, △△을 개선하면 더 성장할 수 있습니다.",
 *   "strengths": ["영상 편집 퀄리티가 뛰어남", "전문성 높은 내용 제공"],
 *   "opportunities": ["업로드 빈도 증가", "더 다양한 시리즈 기획"],
 *   "nextActions": ["업로드 일정표 작성", "시청자 피드백 수집", "콜라보 영상 기획"]
 * }
 */
record OllamaInsightResponse(
    String summary,
    List<String> strengths,
    List<String> opportunities,
    List<String> nextActions
) {
}