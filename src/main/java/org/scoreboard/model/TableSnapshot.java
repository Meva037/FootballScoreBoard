package org.scoreboard.model;

public record TableSnapshot(
        byte[] json,
        String etag,
        long timestamp
) {
    public static TableSnapshot empty() {
        return new TableSnapshot(new byte[0], "0", 0);
    }
}
