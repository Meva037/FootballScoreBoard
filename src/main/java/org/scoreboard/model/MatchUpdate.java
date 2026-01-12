package org.scoreboard.model;

public record MatchUpdate(String matchId,
                          String homeTeamId,
                          String awayTeamId,
                          int homeScore,
                          int awayScore,
                          boolean isMatchStarted,
                          boolean isMatchFinished) {
}
