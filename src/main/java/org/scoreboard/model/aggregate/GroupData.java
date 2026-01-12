package org.scoreboard.model.aggregate;

import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TeamStanding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    private GroupData(Set<String> teamIds, Map<String, MatchUpdate> matches) {
        this.registeredTeamIds = teamIds;
        this.matches = matches;
        this.standings = calculateStandings(teamIds, matches);
    }

    public GroupData updateMatch(MatchUpdate update) {
        Map<String, MatchUpdate> nextMatches = new HashMap<>(this.matches);
        nextMatches.put(update.matchId(), update);
        return new GroupData(this.registeredTeamIds, nextMatches);
    }

    private static Map<String, TeamStanding> calculateStandings(Set<String> teamIds, Map<String, MatchUpdate> matches) {
        Map<String, TeamStanding> result = teamIds.stream()
                .collect(Collectors.toMap(id -> id, TeamStanding::empty));
        for (MatchUpdate match : matches.values()) {
            if (match.isMatchStarted()) {
                TeamStanding homeStats = TeamStanding.fromMatch(match.homeTeamId(), match);
                result.merge(match.homeTeamId(), homeStats, TeamStanding::add);
                TeamStanding awayStats = TeamStanding.fromMatch(match.awayTeamId(), match);
                result.merge(match.awayTeamId(), awayStats, TeamStanding::add);
            }
        }
        return result;
    }

    public Map<String, TeamStanding> getStandings() {
        return standings;
    }
}
