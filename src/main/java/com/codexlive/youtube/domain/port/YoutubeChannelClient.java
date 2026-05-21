package com.codexlive.youtube.domain.port;

import com.codexlive.youtube.domain.ChannelIdentifier;
import com.codexlive.youtube.domain.ChannelSnapshot;

public interface YoutubeChannelClient {

    ChannelSnapshot fetchChannel(ChannelIdentifier identifier);
}
