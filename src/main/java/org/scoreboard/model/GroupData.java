package org.scoreboard.model;

import java.util.HashMap;
import java.util.Map;

public class GroupData {
    private final Map<String, TeamStanding> standings;

    public GroupData() {
        standings = new HashMap<>();
    }

    public Map<String, TeamStanding> getStandings() {
        return standings;
    }
}
