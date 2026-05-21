package com.codexlive.youtube.infrastructure.openai;

import java.util.List;

record OpenAiInsightResponse(
    String summary,
    List<String> strengths,
    List<String> opportunities,
    List<String> nextActions
) {
}
