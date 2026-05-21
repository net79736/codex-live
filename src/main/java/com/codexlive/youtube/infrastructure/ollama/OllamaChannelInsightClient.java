package com.codexlive.youtube.infrastructure.ollama;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.port.ChannelInsightClient;
import com.codexlive.youtube.infrastructure.ChannelInsightPrompt;

@Component
@Profile("ollama")
public class OllamaChannelInsightClient implements ChannelInsightClient {

    private final String model;
    private final OllamaChatGateway gateway;

    public OllamaChannelInsightClient(
        @Value("${ollama.model:}") String model,
        OllamaChatGateway gateway
    ) {
        this.model = model;
        this.gateway = gateway;
    }

    @Override
    public ChannelInsight generateInsight(ChannelSnapshot snapshot) {
        if (model == null || model.isBlank()) {
            throw new MissingApiKeyException("서비스 설정에 필요한 Ollama 모델 설정이 없습니다.");
        }

        OllamaInsightResponse response;
        try {
            response = gateway.generateInsight(
                new OllamaInsightRequest(model, ChannelInsightPrompt.buildUserInput(snapshot))
            );
        } catch (RuntimeException exception) {
            throw new ExternalApiException("Ollama API 호출에 실패했습니다.", exception);
        }

        try {
            return new ChannelInsight(
                response.summary(),
                response.strengths(),
                response.opportunities(),
                response.nextActions()
            );
        } catch (IllegalArgumentException exception) {
            throw new ExternalApiException("Ollama API 호출에 실패했습니다.", exception);
        }
    }
}
