package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoreboard.model.aggregate.GroupData;
import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;
import org.scoreboard.model.aggregate.PlayoffRound;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class TournamentService {
    private static final String SNAPSHOT_REBUILD_ERROR_MESSAGE = "Error rebuilding snapshot: %s";
    private static final String SNAPSHOT_INITIAL_ERROR_MESSAGE = "Failed to create initial snapshot";
    private static final String GROUP_NOT_REGISTERED_ERROR_MESSAGE = "Group %s not registered!";

    private final ConcurrentMap<String, GroupData> groupsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayoffRound> playoffsMap = new ConcurrentHashMap<>();
    private final AtomicReference<TableSnapshot> globalSnapshot = new AtomicReference<>();

    private final ObjectMapper objectMapper;

    private static final Comparator<TeamStanding> FIFA_RULES = Comparator
            .comparingInt(TeamStanding::points)
            .thenComparingInt(TeamStanding::goalsDifference)
            .thenComparingInt(TeamStanding::goalsScored)
            .reversed();

    public TournamentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        TournamentTable emptyTable = new TournamentTable(Map.of(), null);
        try {
            byte[] initialJson = objectMapper.writeValueAsBytes(emptyTable);
            this.globalSnapshot.set(new TableSnapshot(initialJson, "0", System.currentTimeMillis()));
        } catch (Exception e) {
            throw new RuntimeException(SNAPSHOT_INITIAL_ERROR_MESSAGE, e);
        }
    }

    public void createGroup(String groupId, List<String> teamIds) {
        GroupData newGroup = new GroupData(teamIds);
        groupsMap.put(groupId, newGroup);
        rebuildSnapshot();
    }

    public void createPlayoffRound(String roundName, List<MatchUpdate> matches) {
        playoffsMap.put(roundName, new PlayoffRound(matches));
        rebuildSnapshot();
    }

    public void onMatchUpdate(String groupId, MatchUpdate matchUpdate) {
        groupsMap.compute(groupId, (key, currentGroup) -> {
            if (currentGroup == null) {
                throw new IllegalStateException(String.format(GROUP_NOT_REGISTERED_ERROR_MESSAGE, groupId));
            }
            return currentGroup.updateMatch(matchUpdate);
        });
        rebuildSnapshot();
    }

    private synchronized void rebuildSnapshot() {
        try {
            Map<String, List<TeamStanding>> sortedTable = new HashMap<>();
            for (var entry : groupsMap.entrySet()) {
                String groupName = entry.getKey();
                GroupData groupData = entry.getValue();
                List<TeamStanding> sortedTeams = groupData.getStandings().values().stream()
                        .sorted(FIFA_RULES)
                        .toList();
                sortedTable.put(groupName, sortedTeams);
            }
            Map<String, List<MatchUpdate>> playoffsData = new HashMap<>();
            for (var entry : playoffsMap.entrySet()) {
                playoffsData.put(entry.getKey(), entry.getValue().getMatchesList());
            }

            TournamentTable fullTable = new TournamentTable(sortedTable, playoffsData);
            byte[] jsonBytes = objectMapper.writeValueAsBytes(fullTable);
            String etag = "w/" + Arrays.hashCode(jsonBytes);

            globalSnapshot.set(new TableSnapshot(
                    jsonBytes,
                    etag,
                    System.currentTimeMillis()
            ));
        } catch (Exception e) {
            System.err.printf(String.format(SNAPSHOT_REBUILD_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public TableSnapshot getLatestTable() {
        return globalSnapshot.get();
    }
}
