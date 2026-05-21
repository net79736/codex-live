package com.codexlive.youtube.domain.port;

import com.codexlive.youtube.domain.ChannelInsight;
import com.codexlive.youtube.domain.ChannelSnapshot;

public interface ChannelInsightClient {

    ChannelInsight generateInsight(ChannelSnapshot snapshot);
}
