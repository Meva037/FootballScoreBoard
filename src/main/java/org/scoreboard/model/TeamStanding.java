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
    private final static String EXCEPTION_MESSAGE = "Cannot add stats of different teams: %s and %s";

    public static TeamStanding empty(String id) {
        return new TeamStanding(id, 0, 0, 0, 0, 0, 0, 0);
    }

    public TeamStanding add(TeamStanding other) {
        if (!this.teamId.equals(other.teamId())) {
            throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, this.teamId, other.teamId())
            );
        }
        return new TeamStanding(
                this.teamId,
                this.played + other.played,
                this.points + other.points,
                this.goalsScored + other.goalsScored,
                this.goalsConceded + other.goalsConceded,
                this.won + other.won,
                this.drawn + other.drawn,
                this.lost + other.lost
        );
    }

    public static TeamStanding fromMatch(String teamId, MatchUpdate match) {
        if (!match.isMatchStarted()) {
            return TeamStanding.empty(teamId);
        }
        boolean isHome = teamId.equals(match.homeTeamId());
        int myScore = isHome ? match.homeScore() : match.awayScore();
        int opponentScore = isHome ? match.awayScore() : match.homeScore();

        int pts = 0, w = 0, d = 0, l = 0;
        if (myScore > opponentScore) { pts = 3; w = 1; }
        else if (myScore == opponentScore) { pts = 1; d = 1; }
        else { l = 1; }

        return new TeamStanding(
                teamId,
                1,
                pts,
                myScore,
                opponentScore,
                w, d, l
        );
    }
}
