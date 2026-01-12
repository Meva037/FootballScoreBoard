package org.scoreboard.model.aggregate;

import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchUpdate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayoffRoundTest {

    @Test
    void shouldPreserveOrderOfMatches() {
        MatchUpdate m1 = new MatchUpdate("m1", "UA", "EN", 0, 0, false, false);
        MatchUpdate m2 = new MatchUpdate("m2", "FR", "DE", 0, 0, false, false);
        MatchUpdate m3 = new MatchUpdate("m3", "IT", "ES", 0, 0, false, false);
        List<MatchUpdate> initialOrder = List.of(m1, m2, m3);

        PlayoffRound round = new PlayoffRound(initialOrder);

        List<MatchUpdate> storedMatches = round.getMatchesList();

        assertEquals(3, storedMatches.size());
        assertEquals("m1", storedMatches.get(0).matchId());
        assertEquals("m2", storedMatches.get(1).matchId()); // Важливо!
        assertEquals("m3", storedMatches.get(2).matchId());
    }

}