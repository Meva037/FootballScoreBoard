package org.scoreboard.model;

public record TableSnapshot(
        byte[] json,
        String etag,
        long timestamp) {
}
