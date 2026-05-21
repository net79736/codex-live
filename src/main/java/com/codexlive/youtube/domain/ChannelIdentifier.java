package com.codexlive.youtube.domain;

import java.util.Objects;

public record ChannelIdentifier(Type type, String value) {

    public ChannelIdentifier {
        Objects.requireNonNull(type, "type must not be null");
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    public enum Type {
        CHANNEL_ID,
        HANDLE,
        USERNAME
    }
}
