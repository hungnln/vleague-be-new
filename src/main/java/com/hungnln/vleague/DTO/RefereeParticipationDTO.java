package com.hungnln.vleague.DTO;

import lombok.*;

import java.util.UUID;
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefereeParticipationDTO {
    private UUID refereeId;
    private UUID matchId;
    private int role;
}
