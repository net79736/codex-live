package com.codexlive.youtube.infrastructure.ollama;

/**
 * Ollama API 요청을 위한 레코드 (모델, 사용자 입력)
 */
record OllamaInsightRequest(String model, String input) {
}
