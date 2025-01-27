package com.example.bookjourneybackend.global.util;

import lombok.Getter;

@Getter
public enum HttpHeader {

    AUTHORIZATION("Authorization"),
    BEARER("Bearer ");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
