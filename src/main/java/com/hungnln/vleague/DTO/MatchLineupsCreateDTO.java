package com.hungnln.vleague.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchLineupsCreateDTO {
    @NotNull(message = ValidationMessage.MATCHID_VALID_MESSAGE)
    private UUID matchId;
    @NotNull(message = ValidationMessage.START_DATE_VALID_MESSAGE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @NotNull(message = ValidationMessage.STADIUM_VALID_MESSAGE)
    private UUID stadiumId;
    @NotEmpty(message = ValidationMessage.PLAYER_PARTICIPATION_VALID_MESSAGE)
    private List<PlayerParticipationDTO> playerParticipation;
    @NotEmpty(message = ValidationMessage.STAFF_PARTICIPATION_VALID_MESSAGE)
    private List<StaffParticipationDTO> staffParticipation;
    @NotEmpty(message = ValidationMessage.REFEREE_PARTICIPATION_VALID_MESSAGE)
    private List<RefereeParticipationDTO> refereeParticipation;
}
