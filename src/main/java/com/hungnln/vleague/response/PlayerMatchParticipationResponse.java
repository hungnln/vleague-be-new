package com.hungnln.vleague.response;

import com.hungnln.vleague.constant.activity.PlayerMatchRole;
import com.hungnln.vleague.entity.PlayerContract;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class PlayerMatchParticipationResponse {
    private PlayerMatchRole role;
    private boolean inLineups;
    private PlayerContract playerContract;
}
