package com.codexlive.youtube.interfaces;

import com.codexlive.youtube.application.ChannelAnalysisResult;
import java.util.List;

public record ChannelAnalysisResponse(ChannelResponse channel, InsightResponse insight) {

    public static ChannelAnalysisResponse from(ChannelAnalysisResult result) {
        return new ChannelAnalysisResponse(
            new ChannelResponse(
                result.channel().id(),
                result.channel().title(),
                result.channel().description(),
                result.channel().thumbnailUrl(),
                result.channel().subscriberCount(),
                result.channel().viewCount(),
                result.channel().videoCount()
            ),
            new InsightResponse(
                result.insight().summary(),
                result.insight().strengths(),
                result.insight().opportunities(),
                result.insight().nextActions()
            )
        );
    }

    public record ChannelResponse(
        String id,
        String title,
        String description,
        String thumbnailUrl,
        long subscriberCount,
        long viewCount,
        long videoCount
    ) {
    }

    public record InsightResponse(
        String summary,
        List<String> strengths,
        List<String> opportunities,
        List<String> nextActions
    ) {
    }
}
