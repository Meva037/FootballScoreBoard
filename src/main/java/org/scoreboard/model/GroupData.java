package org.scoreboard.model;

import java.util.*;
import java.util.stream.Collectors;

public class GroupData {
    private final Map<String, MatchUpdate> matches;
    private final Map<String, TeamStanding> standings;
    private final Set<String> registeredTeamIds;

    public GroupData(Collection<String> teamIds) {
        this.matches = Map.of();
        this.registeredTeamIds = new HashSet<>(teamIds);
        this.standings = teamIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        TeamStanding::empty
                ));
    }

    public Map<String, TeamStanding> getStandings() {
        return standings;
    }
}
