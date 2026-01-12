package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupDataTest {

    @Test
    void shouldInitializeWithAllTeams_HavingZeroStats() {
        List<String> teamIds = List.of("PL", "EN", "IT", "FR");

        GroupData group = new GroupData(teamIds);

        Map<String, TeamStanding> table = group.getStandings();
        assertEquals(4, table.size());

        teamIds.forEach(teamId -> assertTrue(table.containsKey(teamId)));
        TeamStanding pl = table.get("PL");
        assertEquals(0, pl.points());
        assertEquals(0, pl.played());
        assertEquals(0, pl.goalsScored());
        assertEquals(0, pl.goalsConceded());
        assertEquals(0, pl.won());
        assertEquals(0, pl.drawn());
        assertEquals(0, pl.lost());
    }

    @Test
    void shouldUpdateOnlyPlayingTeams_OthersRemainZero() {
        GroupData group = new GroupData(List.of("PL", "EN", "IT", "FR"));
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 2, 0, true, true);

        group = group.updateMatch(match);
        Map<String, TeamStanding> table = group.getStandings();

        assertEquals(4, table.size());
        assertEquals(3, table.get("PL").points());
        assertEquals(2, table.get("PL").goalsDifference());
        assertEquals(0, table.get("EN").points());
        assertEquals(-2, table.get("EN").goalsDifference());
        assertEquals(0, table.get("IT").points());
        assertEquals(0, table.get("IT").goalsDifference());
    }
}