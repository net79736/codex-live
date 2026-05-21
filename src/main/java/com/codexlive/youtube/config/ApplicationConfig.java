package com.codexlive.youtube.config;

import com.codexlive.youtube.application.AnalyzeChannelUseCase;
import com.codexlive.youtube.application.MissingApiKeyException;
import com.codexlive.youtube.domain.port.ChannelInsightClient;
import com.codexlive.youtube.domain.port.YoutubeChannelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public AnalyzeChannelUseCase analyzeChannelUseCase(
        YoutubeChannelClient youtubeChannelClient,
        ChannelInsightClient channelInsightClient
    ) {
        return new AnalyzeChannelUseCase(youtubeChannelClient, channelInsightClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public YoutubeChannelClient youtubeChannelClient() {
        return identifier -> {
            throw new MissingApiKeyException("서비스 설정에 필요한 YouTube API 키가 없습니다.");
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelInsightClient channelInsightClient() {
        return snapshot -> {
            throw new MissingApiKeyException("서비스 설정에 필요한 OpenAI API 키가 없습니다.");
        };
    }
}
