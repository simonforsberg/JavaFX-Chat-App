package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for messages received from a Ntfy server.
 * Unknown JSON fields are ignored during deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NtfyMessageDto(String id, long time, String event, String topic, String message) {

    @Override
    public String toString() {
        return message;
    }
}