package org.scoreboard.model;

public record MatchUpdate(String matchId,
                          String homeTeamId,
                          String awayTeamId,
                          int homeScore,
                          int awayScore,
                          Integer homePenalties,
                          Integer awayPenalties,
                          boolean isMatchStarted,
                          boolean isMatchFinished) {
    public MatchUpdate(String id, String h, String a, int hs, int as, boolean started, boolean finished) {
        this(id, h, a, hs, as, null, null, started, finished);
    }
}
