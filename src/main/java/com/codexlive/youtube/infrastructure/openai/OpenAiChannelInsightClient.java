package com.codexlive.youtube.infrastructure.openai;

import com.codexlive.youtube.application.ExternalApiException;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.port.ChannelInsightClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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
            response = gateway.generateInsight(apiKey, new OpenAiInsightRequest(model, buildInput(snapshot)));
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

    private String buildInput(ChannelSnapshot snapshot) {
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
}
