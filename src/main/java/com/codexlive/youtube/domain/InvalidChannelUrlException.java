package com.codexlive.youtube.domain;

public class InvalidChannelUrlException extends RuntimeException {

    public InvalidChannelUrlException(String message) {
        super(message);
    }
}
