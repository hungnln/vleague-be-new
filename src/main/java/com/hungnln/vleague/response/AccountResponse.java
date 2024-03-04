package com.hungnln.vleague.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountResponse {
    private UUID id;
    private String email;
    private String name;
    private String imageURL;
    @JsonProperty("isBanned")
    private boolean isBanned;

}
