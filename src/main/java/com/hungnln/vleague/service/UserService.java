package com.hungnln.vleague.service;

import com.hungnln.vleague.constant.role.Role;
import com.hungnln.vleague.entity.Account;
import com.hungnln.vleague.entity.CustomUserDetails;
import com.hungnln.vleague.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        return CustomUserDetails.builder()
                .account(account)
                .roles(roles)
                .build();
    }

    public UserDetails loadUserById(UUID userId) {
        List<Role> roles = new ArrayList<>();
        Account account = accountRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException(userId.toString()));
        roles.add(Role.ROLE_USER);

        return new CustomUserDetails(account, List.of(Role.ROLE_USER));
    }
}
