package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;
import org.scoreboard.model.aggregate.GroupData;
import org.scoreboard.model.aggregate.PlayoffRound;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SnapshotBuilder {

    private static final String SNAPSHOT_REBUILD_ERROR_MESSAGE = "Error rebuilding snapshot: %s";
    private static final Comparator<TeamStanding> FIFA_RULES = Comparator
            .comparingInt(TeamStanding::points)
            .thenComparingInt(TeamStanding::goalsDifference)
            .thenComparingInt(TeamStanding::goalsScored)
            .reversed();

    private final ObjectMapper objectMapper;

    public SnapshotBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TableSnapshot build(Map<String, GroupData> groups, Map<String, PlayoffRound> playoffs) {
        try {
            Map<String, List<TeamStanding>> sortedTable = new HashMap<>();
            for (var entry : groups.entrySet()) {
                String groupName = entry.getKey();
                GroupData groupData = entry.getValue();
                List<TeamStanding> sortedTeams = groupData.getStandings().values().stream()
                        .sorted(FIFA_RULES)
                        .toList();
                sortedTable.put(groupName, sortedTeams);
            }
            Map<String, List<MatchUpdate>> playoffsData = new HashMap<>();
            for (var entry : playoffs.entrySet()) {
                playoffsData.put(entry.getKey(), entry.getValue().getMatchesList());
            }

            TournamentTable fullTable = new TournamentTable(sortedTable, playoffsData);
            byte[] jsonBytes = objectMapper.writeValueAsBytes(fullTable);
            String etag = "w/" + Arrays.hashCode(jsonBytes);

            return new TableSnapshot(
                    jsonBytes,
                    etag,
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            throw new RuntimeException(String.format(SNAPSHOT_REBUILD_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
