package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoreboard.model.GroupData;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class TournamentService {
    private final static String SNAPSHOT_REBUILD_ERROR_MESSAGE = "Error rebuilding snapshot: %s";
    private final static String SNAPSHOT_INITIAL_ERROR_MESSAGE = "Failed to create initial snapshot";

    private final ConcurrentMap<String, GroupData> groupsMap = new ConcurrentHashMap<>();
    private final AtomicReference<TableSnapshot> globalSnapshot = new AtomicReference<>();

    private final ObjectMapper objectMapper;

    public TournamentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        TournamentTable emptyTable = new TournamentTable(Map.of());
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

    private synchronized void rebuildSnapshot() {
        try {
            Map<String, List<TeamStanding>> table = new HashMap<>();
            for (var entry : groupsMap.entrySet()) {
                String groupName = entry.getKey();
                GroupData groupData = entry.getValue();
                List<TeamStanding> sortedTeams = groupData.getStandings().values().stream().toList();
                table.put(groupName, sortedTeams);
            }

            TournamentTable fullTable = new TournamentTable(table);
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
