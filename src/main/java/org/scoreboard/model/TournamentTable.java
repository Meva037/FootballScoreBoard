package org.scoreboard.model;

import java.util.List;
import java.util.Map;

public record TournamentTable(Map<String, List<TeamStanding>> groups,
                              Map<String, List<MatchUpdate>> playoffs ) {}
