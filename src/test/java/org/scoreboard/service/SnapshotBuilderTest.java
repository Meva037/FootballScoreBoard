package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;
import org.scoreboard.model.aggregate.GroupData;
import org.scoreboard.model.aggregate.PlayoffRound;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotBuilderTest {

    private SnapshotBuilder builder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        builder = new SnapshotBuilder(objectMapper);
    }

    @Test
    void shouldBuildSnapshot_FromEmptyData() throws IOException {
        Map<String, GroupData> emptyGroups = Map.of();
        Map<String, PlayoffRound> emptyPlayoffs = Map.of();

        TableSnapshot snapshot = builder.build(emptyGroups, emptyPlayoffs);

        assertNotNull(snapshot);
        assertNotNull(snapshot.etag());
        TournamentTable result = objectMapper.readValue(snapshot.json(), TournamentTable.class);
        assertTrue(result.groups().isEmpty());
        assertTrue(result.playoffs().isEmpty());
    }

    @Test
    void shouldSortGroupStandings_InOutputJson() throws IOException {
        List<String> teams = List.of("EN", "PL");
        GroupData group = new GroupData(teams);
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 1, 0, true, true);
        group = group.updateMatch(match);
        Map<String, GroupData> groups = Map.of("Group A", group);

        TableSnapshot snapshot = builder.build(groups, Map.of());

        TournamentTable result = objectMapper.readValue(snapshot.json(), TournamentTable.class);
        List<TeamStanding> standings = result.groups().get("Group A");
        assertEquals("PL", standings.get(0).teamId());
        assertEquals(3, standings.get(0).points());
        assertEquals("EN", standings.get(1).teamId());
    }

    @Test
    void shouldIncludePlayoffMatches() throws IOException {
        MatchUpdate finalMatch = new MatchUpdate("f1", "PL", "EN", 2, 1, true, true);
        PlayoffRound finalRound = new PlayoffRound(List.of(finalMatch));
        Map<String, PlayoffRound> playoffs = Map.of("Final", finalRound);

        TableSnapshot snapshot = builder.build(Map.of(), playoffs);

        TournamentTable result = objectMapper.readValue(snapshot.json(), TournamentTable.class);
        assertTrue(result.playoffs().containsKey("Final"));
        assertEquals("PL", result.playoffs().get("Final").getFirst().homeTeamId());
    }
}