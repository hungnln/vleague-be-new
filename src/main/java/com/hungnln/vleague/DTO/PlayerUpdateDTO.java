package com.hungnln.vleague.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import com.hungnln.vleague.constant.validation_size.ValidationSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerUpdateDTO {
    @NotEmpty(message = ValidationMessage.IMAGE_VALID_MESSAGE)
    private String imageURL;

    @NotEmpty(message = ValidationMessage.NAME_VALID_MESSAGE)
    private String name;

    @NotNull(message = ValidationMessage.DOB_VALID_MESSAGE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;


    @NotNull(message = ValidationMessage.DOB_VALID_MESSAGE)
    @Min(ValidationSize.HEIGHTCM_MIN)
    @Max(ValidationSize.HEIGHTCM_MAX)
    private float heightCm;

    @Min(ValidationSize.WEIGHTKG_MIN)
    @Max(ValidationSize.WEIGHTKG_MAX)
    @NotNull(message = ValidationMessage.DOB_VALID_MESSAGE)
    private float weightKg;
}
