package com.hungnln.vleague.DTO;

import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.UUID;
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffParticipationDTO {
    @NotEmpty(message = ValidationMessage.STAFF_CONTRACT_VALID_MESSAGE)
    private UUID staffContractId;
    @NotEmpty(message = ValidationMessage.MATCHID_VALID_MESSAGE)
    private UUID matchId;
    @NotEmpty(message = ValidationMessage.POSITION_VALID_MESSAGE)
    private int role;
}
