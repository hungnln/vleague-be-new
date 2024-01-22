package com.hungnln.vleague.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TournamentRankingResponse {
    List<PlayerRankingResponse> yellowCards;
    List<PlayerRankingResponse> redCards;
    List<PlayerRankingResponse> goals;


}
