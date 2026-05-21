package com.codexlive.youtube.infrastructure.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponseCreateParams;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class SdkOpenAiResponsesGateway implements OpenAiResponsesGateway {

    @Override
    public OpenAiInsightResponse generateInsight(String apiKey, OpenAiInsightRequest request) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .build();
        StructuredResponseCreateParams<StructuredInsight> params = ResponseCreateParams.builder()
            .input(request.input())
            .text(StructuredInsight.class)
            .model(ChatModel.of(request.model()))
            .build();

        StructuredInsight insight = client.responses().create(params).output().stream()
            .flatMap(item -> item.message().stream())
            .flatMap(message -> message.content().stream())
            .flatMap(content -> content.outputText().stream())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("OpenAI structured output is empty"));

        return new OpenAiInsightResponse(
            insight.summary,
            insight.strengths,
            insight.opportunities,
            insight.nextActions
        );
    }

    public static class StructuredInsight {
        public String summary;
        public List<String> strengths;
        public List<String> opportunities;
        public List<String> nextActions;
    }
}
