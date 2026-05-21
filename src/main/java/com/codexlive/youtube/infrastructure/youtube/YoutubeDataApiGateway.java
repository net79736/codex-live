package com.codexlive.youtube.infrastructure.youtube;

import com.codexlive.youtube.domain.ChannelIdentifier;

interface YoutubeDataApiGateway {

    YoutubeChannelApiResponse fetchChannel(String apiKey, ChannelIdentifier identifier);
}
