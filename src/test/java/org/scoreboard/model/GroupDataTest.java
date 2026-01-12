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
}