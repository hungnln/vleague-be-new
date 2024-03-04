package com.hungnln.vleague.response;

import com.hungnln.vleague.entity.Account;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthResponse {
    private Account account;
}
