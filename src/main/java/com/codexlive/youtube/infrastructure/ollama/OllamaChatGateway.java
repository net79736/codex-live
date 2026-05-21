package com.codexlive.youtube.infrastructure.ollama;

/**
 * Ollama API를 호출하여 채널 인사이트를 생성하는 게이트웨이
 */
interface OllamaChatGateway {

    /**
     * Ollama API를 호출하여 채널 인사이트를 생성한다.
     * @param request 채널 인사이트 요청
     * @return 채널 인사이트 응답
     */
    OllamaInsightResponse generateInsight(OllamaInsightRequest request);
}
