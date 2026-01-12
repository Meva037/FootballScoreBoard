package org.scoreboard.model;

public record TeamStanding(
        String teamId,
        int played,
        int points,
        int goalsScored,
        int goalsConceded,
        int won,
        int drawn,
        int lost
) {
    public static TeamStanding empty(String id) {
        return new TeamStanding(id, 0, 0, 0, 0, 0, 0, 0);
    }
}
