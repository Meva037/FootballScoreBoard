package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TournamentServiceTest {
    private TournamentService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TournamentService(objectMapper);
    }

    @Test
    void shouldReturnEmptyJson_WhenNoGroupsCreated() throws IOException {
        TableSnapshot snapshot = service.getLatestTable();

        TournamentTable table = objectMapper.readValue(snapshot.json(), TournamentTable.class);
        assertTrue(table.groups().isEmpty());
    }

    @Test
    void shouldReturnTableWithZeroStats_WhenGroupIsRegistered() throws IOException {
        List<String> teams = List.of("PL", "EN");

        service.createGroup("Group A", teams);
        TableSnapshot snapshot = service.getLatestTable();
        TournamentTable table = objectMapper.readValue(snapshot.json(), TournamentTable.class);

        assertTrue(table.groups().containsKey("Group A"));
        List<TeamStanding> standings = table.groups().get("Group A");
        assertEquals(2, standings.size());
        TeamStanding plStats = standings.stream()
                .filter(t -> t.teamId().equals("PL"))
                .findFirst()
                .orElseThrow();
        assertEquals(0, plStats.points());
        assertEquals(0, plStats.played());
        assertEquals(0, plStats.goalsScored());
    }

    @Test
    void shouldUpdateStats_WhenMatchPlayed() throws IOException {
        List<String> teams = List.of("PL", "EN", "IT", "FR");
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 3, 0, true, true);

        service.createGroup("Group A", teams);
        service.onMatchUpdate("Group A", match);

        TournamentTable table = objectMapper.readValue(service.getLatestTable().json(), TournamentTable.class);
        TeamStanding plStats = table.groups().get("Group A").getFirst();
        TeamStanding enStats = table.groups().get("Group A").getLast();
        assertEquals("PL", plStats.teamId());
        assertEquals(3, plStats.points());
        assertEquals(3, plStats.goalsScored());
        assertEquals("EN", enStats.teamId());
        assertEquals(0, enStats.points());
        assertEquals(-3, enStats.goalsDifference());
    }

    @Test
    void shouldThrowException_WhenMatchUpdateOfNotRegisteredGroup() {
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 3, 0, true, true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.onMatchUpdate("Group A", match);;
        });

        assertEquals("Group Group A not registered!", exception.getMessage());
    }

    @Test
    void shouldCreatePlayoffRound_WithEmptyMatches() throws IOException {
        MatchUpdate finalMatch = new MatchUpdate("m_final", "TBD", "TBD", 0, 0, false, false);

        service.createPlayoffRound("Final", List.of(finalMatch));

        TableSnapshot snapshot = service.getLatestTable();
        TournamentTable table = objectMapper.readValue(snapshot.json(), TournamentTable.class);
        assertTrue(table.playoffs().containsKey("Final"));
        assertEquals("TBD", table.playoffs().get("Final").getFirst().homeTeamId());
    }
}