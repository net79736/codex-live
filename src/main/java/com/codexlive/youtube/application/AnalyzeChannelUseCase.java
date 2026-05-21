package com.codexlive.youtube.application;

import com.codexlive.youtube.domain.ChannelIdentifier;
import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;
import com.codexlive.youtube.domain.ChannelUrlParser;
import com.codexlive.youtube.domain.port.ChannelInsightClient;
import com.codexlive.youtube.domain.port.YoutubeChannelClient;
import java.util.Objects;

public class AnalyzeChannelUseCase {

    private final ChannelUrlParser channelUrlParser;
    private final YoutubeChannelClient youtubeChannelClient;
    private final ChannelInsightClient channelInsightClient;

    public AnalyzeChannelUseCase(YoutubeChannelClient youtubeChannelClient, ChannelInsightClient channelInsightClient) {
        this(new ChannelUrlParser(), youtubeChannelClient, channelInsightClient);
    }

    public AnalyzeChannelUseCase(
        ChannelUrlParser channelUrlParser,
        YoutubeChannelClient youtubeChannelClient,
        ChannelInsightClient channelInsightClient
    ) {
        this.channelUrlParser = Objects.requireNonNull(channelUrlParser, "channelUrlParser must not be null");
        this.youtubeChannelClient = Objects.requireNonNull(youtubeChannelClient, "youtubeChannelClient must not be null");
        this.channelInsightClient = Objects.requireNonNull(channelInsightClient, "channelInsightClient must not be null");
    }

    public ChannelAnalysisResult analyze(AnalyzeChannelCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        ChannelIdentifier identifier = channelUrlParser.parse(command.channelUrl());
        ChannelSnapshot channel = youtubeChannelClient.fetchChannel(identifier);
        ChannelInsight insight = channelInsightClient.generateInsight(channel);

        return new ChannelAnalysisResult(channel, insight);
    }
}
