package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamStandingTest {

    @Test
    void shouldCalculateWinCorrectly() {
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 2, 1, true, true);

        TeamStanding stats = TeamStanding.fromMatch("PL", match);

        assertEquals(1, stats.played());
        assertEquals(3, stats.points());
        assertEquals(1, stats.won());
        assertEquals(0, stats.drawn());
        assertEquals(0, stats.lost());
        assertEquals(2, stats.goalsScored());
        assertEquals(1, stats.goalsConceded());
    }

    @Test
    void shouldCalculateDrawCorrectly() {
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 1, 1, true, true);
        TeamStanding stats = TeamStanding.fromMatch("PL", match);

        assertEquals(1, stats.played());
        assertEquals(1, stats.points());
        assertEquals(0, stats.won());
        assertEquals(1, stats.drawn());
        assertEquals(0, stats.lost());
        assertEquals(1, stats.goalsScored());
        assertEquals(1, stats.goalsConceded());
    }

    @Test
    void shouldCalculateLoseCorrectly() {
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 0, 3, true, true);

        TeamStanding stats = TeamStanding.fromMatch("PL", match);

        assertEquals(1, stats.played());
        assertEquals(0, stats.points());
        assertEquals(0, stats.won());
        assertEquals(0, stats.drawn());
        assertEquals(1, stats.lost());
        assertEquals(0, stats.goalsScored());
        assertEquals(3, stats.goalsConceded());
    }

}