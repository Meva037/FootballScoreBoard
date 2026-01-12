package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupDataTest {

    @Test
    void newGroup_ShouldHaveNoStandings() {
        GroupData group = new GroupData();

        assertTrue(group.getStandings().isEmpty());
    }
}