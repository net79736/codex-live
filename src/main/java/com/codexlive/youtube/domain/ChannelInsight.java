package com.codexlive.youtube.domain;

import java.util.List;

public record ChannelInsight(
    String summary,
    List<String> strengths,
    List<String> opportunities,
    List<String> nextActions
) {

    public ChannelInsight {
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }
        strengths = List.copyOf(requireNonEmpty(strengths, "strengths"));
        opportunities = List.copyOf(requireNonEmpty(opportunities, "opportunities"));
        nextActions = List.copyOf(requireNonEmpty(nextActions, "nextActions"));
    }

    private static List<String> requireNonEmpty(List<String> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return values;
    }
}
