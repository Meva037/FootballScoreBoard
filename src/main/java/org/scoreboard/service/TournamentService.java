package org.scoreboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoreboard.model.aggregate.GroupData;
import org.scoreboard.model.MatchUpdate;
import org.scoreboard.model.TableSnapshot;
import org.scoreboard.model.TeamStanding;
import org.scoreboard.model.TournamentTable;
import org.scoreboard.model.aggregate.PlayoffRound;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class TournamentService {
    private static final String SNAPSHOT_INITIAL_ERROR_MESSAGE = "Failed to create initial snapshot";
    private static final String UNKNOWN_STAGE_ERROR_MESSAGE = "Unknown stage: %s";
    private static final Comparator<TeamStanding> FIFA_RULES = Comparator
            .comparingInt(TeamStanding::points)
            .thenComparingInt(TeamStanding::goalsDifference)
            .thenComparingInt(TeamStanding::goalsScored)
            .reversed();

    private final ConcurrentMap<String, GroupData> groupsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayoffRound> playoffsMap = new ConcurrentHashMap<>();
    private final AtomicReference<TableSnapshot> globalSnapshot = new AtomicReference<>();

    private final SnapshotBuilder snapshotBuilder;

    public TournamentService(ObjectMapper objectMapper) {
        this.snapshotBuilder = new SnapshotBuilder(objectMapper);
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

    public void onMatchUpdate(String stageId, MatchUpdate matchUpdate) {
        if (groupsMap.containsKey(stageId)) {
            groupsMap.compute(stageId, (k, v) -> v.updateMatch(matchUpdate));
        } else if (playoffsMap.containsKey(stageId)) {
            playoffsMap.compute(stageId, (k, v) -> v.updateMatch(matchUpdate));
        } else {
            throw new IllegalStateException(String.format(UNKNOWN_STAGE_ERROR_MESSAGE, stageId));
        }
        rebuildSnapshot();
    }

    private synchronized void rebuildSnapshot() {
        globalSnapshot.set(snapshotBuilder.build(groupsMap, playoffsMap));
    }

    public TableSnapshot getLatestTable() {
        return globalSnapshot.get();
    }
}
