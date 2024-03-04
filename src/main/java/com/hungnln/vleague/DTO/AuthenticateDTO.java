package com.hungnln.vleague.DTO;

import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateDTO {
    @NotEmpty(message = ValidationMessage.USERNAME_VALID_MESSAGE)
    private String username;
    @NotEmpty(message = ValidationMessage.PASSWORD_VALID_MESSAGE)
    private String password;
}
