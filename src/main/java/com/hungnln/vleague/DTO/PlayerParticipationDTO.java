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
public class PlayerParticipationDTO {
    @NotEmpty(message = ValidationMessage.PLAYER_CONTRACT_VALID_MESSAGE)
    private UUID playerContractId;
    @NotEmpty(message = ValidationMessage.MATCHID_VALID_MESSAGE)
    private UUID matchId;
    @NotEmpty(message = ValidationMessage.POSITION_VALID_MESSAGE)
    private int role;
    @NotEmpty(message = ValidationMessage.INLINEUPS_VALID_MESSAGE)
    private boolean inLineups;
}
