package com.codexlive.youtube.infrastructure.openai;

interface OpenAiResponsesGateway {

    OpenAiInsightResponse generateInsight(String apiKey, OpenAiInsightRequest request);
}
