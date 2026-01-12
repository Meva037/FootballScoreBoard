package org.scoreboard.model.aggregate;

import org.scoreboard.model.MatchUpdate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayoffRound {

    private final Map<String, MatchUpdate> matches;

    public PlayoffRound(List<MatchUpdate> initialMatches) {
        this.matches = new LinkedHashMap<>();
        for (MatchUpdate m : initialMatches) {
            this.matches.put(m.matchId(), m);
        }
    }

    public List<MatchUpdate> getMatchesList() {
        return new ArrayList<>(matches.values());
    }

}
