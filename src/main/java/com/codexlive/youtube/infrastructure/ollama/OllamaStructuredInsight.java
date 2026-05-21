package com.codexlive.youtube.infrastructure.ollama;

import java.util.List;

/**
 * Ollama API 응답을 위한 레코드 (요약, 강점, 기회, 다음 액션)
 */
class OllamaStructuredInsight {
    public String summary;
    public List<String> strengths;
    public List<String> opportunities;
    public List<String> nextActions;
}
