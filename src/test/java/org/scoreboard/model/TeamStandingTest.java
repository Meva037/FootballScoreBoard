package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void shouldAggregateStats() {
        TeamStanding match1 = new TeamStanding("PL", 1, 3, 2, 0, 1, 0, 0);
        TeamStanding match2 = new TeamStanding("PL", 1, 1, 1, 1, 0, 1, 0);

        TeamStanding total = match1.add(match2);

        assertEquals(2, total.played());
        assertEquals(4, total.points());
        assertEquals(3, total.goalsScored());
    }

    @Test
    void shouldThrowException_WhenAddingStatsOfDifferentTeams() {
        TeamStanding en = new TeamStanding("EN", 1, 3, 2, 0, 1, 0, 0);
        TeamStanding pl = new TeamStanding("PL", 1, 1, 1, 1, 0, 1, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            en.add(pl);
        });

        assertEquals("Cannot add stats of different teams: EN and PL", exception.getMessage());
    }

}