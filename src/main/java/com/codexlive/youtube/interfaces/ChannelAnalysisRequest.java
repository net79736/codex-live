package com.codexlive.youtube.interfaces;

import jakarta.validation.constraints.NotBlank;

public record ChannelAnalysisRequest(
    @NotBlank(message = "채널 URL을 입력하세요.")
    String channelUrl
) {
}
