package com.codexlive.youtube.infrastructure.ollama;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.codexlive.youtube.infrastructure.ChannelInsightPrompt;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Profile("ollama")
class RestOllamaChatGateway implements OllamaChatGateway {

    private final String baseUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    RestOllamaChatGateway(
        @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
        ObjectMapper objectMapper
    ) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override
    public OllamaInsightResponse generateInsight(OllamaInsightRequest request) {
        OllamaChatApiRequest apiRequest = new OllamaChatApiRequest(
            request.model(),
            false,
            "json",
            List.of(
                new OllamaChatMessage("system", ChannelInsightPrompt.systemJsonInstruction()),
                new OllamaChatMessage("user", request.input())
            )
        );

        OllamaChatApiResponse apiResponse = restClient.post()
            .uri("/api/chat")
            .body(apiRequest)
            .retrieve()
            .body(OllamaChatApiResponse.class);

        if (apiResponse == null || apiResponse.message() == null || apiResponse.message().content() == null) {
            throw new IllegalStateException("Ollama response is empty");
        }

        try {
            OllamaStructuredInsight insight = objectMapper.readValue(
                apiResponse.message().content(),
                OllamaStructuredInsight.class
            );
            if (insight.summary == null || insight.summary.isBlank()) {
                throw new IllegalStateException("Ollama JSON summary is blank");
            }
            return new OllamaInsightResponse(
                insight.summary,
                insight.strengths,
                insight.opportunities,
                insight.nextActions
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to parse Ollama JSON response", exception);
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:11434";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private record OllamaChatApiRequest(
        String model,
        boolean stream,
        String format,
        List<OllamaChatMessage> messages
    ) {
    }

    private record OllamaChatMessage(String role, String content) {
    }

    private record OllamaChatApiResponse(OllamaChatMessage message) {
    }
}
