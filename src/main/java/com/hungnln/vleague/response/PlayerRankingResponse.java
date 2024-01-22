package com.hungnln.vleague.response;

import com.hungnln.vleague.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRankingResponse {
    Player player;
    int count =0;
}
