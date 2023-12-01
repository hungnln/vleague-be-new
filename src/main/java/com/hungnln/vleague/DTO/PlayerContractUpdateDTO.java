package com.hungnln.vleague.DTO;

import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerContractUpdateDTO {
    @NotEmpty(message = ValidationMessage.NAME_VALID_MESSAGE)
    private String description;

    @NotEmpty(message = ValidationMessage.NUMBER_VALID_MESSAGE)
    private int number;

    @NotEmpty(message = ValidationMessage.SALARY_VALID_MESSAGE)
    private float salary;

    @NotEmpty(message = ValidationMessage.END_DATE_VALID_MESSAGE)
    private Date end;

}
