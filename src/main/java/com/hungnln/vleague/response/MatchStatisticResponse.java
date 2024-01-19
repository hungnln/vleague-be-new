package com.hungnln.vleague.response;

import com.hungnln.vleague.constant.activity.ActivityType;
import com.hungnln.vleague.entity.Match;
import lombok.*;

import java.util.*;

@Data
@ToString
@AllArgsConstructor
@Builder
public class MatchStatisticResponse {
    Map<UUID,Map<ActivityType,Integer>> matchStatistic;
    public MatchStatisticResponse(Match match) {
        this.matchStatistic = new HashMap<>();
        // Initialize the map with default values for each ActivityType
        for (ActivityType type : ActivityType.values()) {
            for (UUID clubId : getAllClubMatch(match)) {
                this.matchStatistic.computeIfAbsent(clubId, k -> new HashMap<>()).put(type, 0);
            }
        }
    }
    private Set<UUID> getAllClubMatch(Match match) {
        // Replace this with your logic to fetch home club IDs from your data
        Set<UUID> clubList = new HashSet<>();
        clubList.add(match.getHomeClub().getId());
        clubList.add(match.getAwayClub().getId());
        // Add logic to fetch home club IDs from your data and populate homeClubIds
        return clubList;
    }
    public void setDefaultValues() {
        for (ActivityType type : ActivityType.values()) {
            for (UUID homeClubId : matchStatistic.keySet()) {
                matchStatistic.computeIfAbsent(homeClubId, k -> new HashMap<>()).put(type, 0);
            }
        }
    }
}
