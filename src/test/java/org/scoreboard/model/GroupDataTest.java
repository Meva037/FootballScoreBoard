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
        TeamStanding plStats = table.get("PL");
        assertTeamStandingEquals(0, 0, 0, 0, 0, 0, 0, plStats);

    }

    @Test
    void shouldUpdateOnlyPlayingTeams_OthersRemainZero() {
        GroupData group = new GroupData(List.of("PL", "EN", "IT", "FR"));
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 2, 0, true, true);

        group = group.updateMatch(match);
        Map<String, TeamStanding> table = group.getStandings();

        assertEquals(4, table.size());
        TeamStanding plStats = table.get("PL");
        TeamStanding enStats = table.get("EN");
        TeamStanding itStats = table.get("IT");
        assertTeamStandingEquals(1, 3, 2, 0, 1, 0, 0, plStats);
        assertTeamStandingEquals(1, 0, 0, 2, 0, 0, 1, enStats);
        assertTeamStandingEquals(0, 0, 0, 0, 0, 0, 0, itStats);
    }

    @Test
    void shouldAggregatePoints_FromDifferentMatches() {
        GroupData group = new GroupData(List.of("PL", "EN", "IT", "FR"));
        group = group.updateMatch(new MatchUpdate("m1", "PL", "EN", 1, 0, true, true));
        group = group.updateMatch(new MatchUpdate("m2", "PL", "IT", 1, 1, true, true));

        Map<String, TeamStanding> table = group.getStandings();

        TeamStanding plStats = table.get("PL");
        TeamStanding enStats = table.get("EN");
        TeamStanding itStats = table.get("IT");
        TeamStanding frStats = table.get("FR");
        assertTeamStandingEquals(2, 4, 2, 1, 1, 1, 0, plStats);
        assertTeamStandingEquals(1, 0, 0, 1, 0, 0, 1, enStats);
        assertTeamStandingEquals(1, 1, 1, 1, 0, 1, 0, itStats);
        assertTeamStandingEquals(0, 0, 0, 0, 0, 0, 0, frStats);
    }

    private void assertTeamStandingEquals(
            int expectedPlayed,
            int expectedPoints,
            int expectedGoalsScored,
            int expectedGoalsConceded,
            int expectedWon,
            int expectedDrawn,
            int expectedLost,
            TeamStanding current
    ) {
        assertEquals(expectedPlayed, current.played());
        assertEquals(expectedPoints, current.points());
        assertEquals(expectedGoalsScored, current.goalsScored());
        assertEquals(expectedGoalsConceded, current.goalsConceded());
        assertEquals(expectedWon, current.won());
        assertEquals(expectedDrawn, current.drawn());
        assertEquals(expectedLost, current.lost());
    }
}