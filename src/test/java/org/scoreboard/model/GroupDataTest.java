package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupDataTest {

    @Test
    void newGroup_ShouldHaveNoStandings() {
        GroupData group = new GroupData();

        assertTrue(group.getStandings().isEmpty());
    }

    @Test
    void shouldCreateStandings_WhenFirstMatchAdded() {
        GroupData group = new GroupData();
        MatchUpdate match = new MatchUpdate("m1", "PL", "EN", 2, 1, true, false);

        GroupData updatedGroup = group.updateMatch(match);
        Map<String, TeamStanding> table = updatedGroup.getStandings();

        assertEquals(2, table.size());
        assertEquals(3, table.get("PL").points());
        assertEquals(0, table.get("EN").points());
        assertEquals(1, table.get("PL").played());
    }
}