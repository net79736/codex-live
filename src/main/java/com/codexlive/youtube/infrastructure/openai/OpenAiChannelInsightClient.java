package com.codexlive.youtube.infrastructure.openai;

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
@Profile("!dev & !ollama")
public class OpenAiChannelInsightClient implements ChannelInsightClient {

    private final String apiKey;
    private final String model;
    private final OpenAiResponsesGateway gateway;

    public OpenAiChannelInsightClient(
        @Value("${openai.api-key:}") String apiKey,
        @Value("${openai.model:}") String model,
        OpenAiResponsesGateway gateway
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.gateway = gateway;
    }

    @Override
    public ChannelInsight generateInsight(ChannelSnapshot snapshot) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingApiKeyException("서비스 설정에 필요한 OpenAI API 키가 없습니다.");
        }
        if (model == null || model.isBlank()) {
            throw new MissingApiKeyException("서비스 설정에 필요한 OpenAI 모델 설정이 없습니다.");
        }

        OpenAiInsightResponse response;
        try {
            response = gateway.generateInsight(
                apiKey,
                new OpenAiInsightRequest(model, ChannelInsightPrompt.buildUserInput(snapshot))
            );
        } catch (RuntimeException exception) {
            throw new ExternalApiException("OpenAI API 호출에 실패했습니다.", exception);
        }

        return new ChannelInsight(
            response.summary(),
            response.strengths(),
            response.opportunities(),
            response.nextActions()
        );
    }
}
