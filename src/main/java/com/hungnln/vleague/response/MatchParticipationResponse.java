package com.hungnln.vleague.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hungnln.vleague.entity.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MatchParticipationResponse {
    private UUID matchId;
    private Match match;
//    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Collection<PlayerMatchParticipation> playerMatchParticipations;
    private Collection<StaffMatchParticipation> staffMatchParticipations;
    private Collection<RefereeMatchParticipation> refereeMatchParticipations;

}
