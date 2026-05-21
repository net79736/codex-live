package com.codexlive.youtube.application;

import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;

public record ChannelAnalysisResult(ChannelSnapshot channel, ChannelInsight insight) {
}
