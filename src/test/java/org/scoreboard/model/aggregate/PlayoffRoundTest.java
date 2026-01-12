package org.scoreboard.model.aggregate;

import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchUpdate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

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

    @Test
    void shouldUpdateMatchResult_AndReturnNewInstance() {
        MatchUpdate start = new MatchUpdate("m1", "PL", "EN", 0, 0, true, false);
        MatchUpdate goal = new MatchUpdate("m1", "PL", "EN", 1, 0, true, false);

        PlayoffRound originalRound = new PlayoffRound(List.of(start));
        PlayoffRound updatedRound = originalRound.updateMatch(goal);

        assertEquals(1, updatedRound.getMatchesList().getFirst().homeScore());
        assertEquals(0, originalRound.getMatchesList().getFirst().homeScore());
        assertNotSame(originalRound, updatedRound);
    }

    @Test
    void shouldStorePenalties_WhenProvided() {
        MatchUpdate m1 = new MatchUpdate("m1", "PL", "EN", 0, 0, true, false);
        PlayoffRound round = new PlayoffRound(List.of(m1));

        MatchUpdate penalties = new MatchUpdate("m1", "PL", "EN", 1, 1, 5, 4, true, true);
        round = round.updateMatch(penalties);

        MatchUpdate stored = round.getMatchesList().getFirst();
        assertEquals(5, stored.homePenalties());
        assertEquals(4, stored.awayPenalties());
    }

}