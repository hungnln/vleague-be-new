package com.hungnln.vleague.service;

import com.google.auth.Credentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hungnln.vleague.DTO.AuthenticateDTO;
import com.hungnln.vleague.constant.role.Role;
import com.hungnln.vleague.entity.Account;
import com.hungnln.vleague.entity.CustomUserDetails;
import com.hungnln.vleague.repository.AccountRepository;
import com.hungnln.vleague.response.AccountLoginResponse;
import com.hungnln.vleague.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    public AccountLoginResponse loginAdmin(AuthenticateDTO authenticateDTO){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticateDTO.getUsername(),
                            authenticateDTO.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Object principal = authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateAdminToken((User) principal);
            Account account = Account.builder()
                    .name(((User) principal).getUsername())
                    .imageURL("123123123")
                    .email(((User) principal).getUsername())
                    .isBanned(false)
                    .build();
            return AccountLoginResponse.builder()
                    .account(account)
                    .token(jwt)
                    .build();


    }
    public AccountLoginResponse firebaseTokenCheckAccount(String token){
        FirebaseToken decodedToken = null;
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(Role.ROLE_USER.toString());
        try{
            if (token != null && !token.equalsIgnoreCase("undefined")) {
                decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            }
        }catch (FirebaseAuthException e) {
            e.printStackTrace();
            log.error("Firebase Exception:: ", e.getLocalizedMessage());
        }

//        String[] split_string = token.split("\\.");
//        String base64EncodedBody = split_string[1];
//        Base64 base64Url = new Base64(true);
//        String body = new String(base64Url.decode(base64EncodedBody));
//        JSONObject jsonObject = new JSONObject(body);
//
//        String email = jsonObject.get("email").toString();
//        String image = jsonObject.get("picture").toString();
//        String name = jsonObject.get("unique_name").toString();
        Account account = accountService.firebaseTokenCheckAccount(decodedToken);
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .account(account)
                .roles(List.of(Role.ROLE_USER))
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails,simpleGrantedAuthority);
        String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        return AccountLoginResponse.builder()
                .token(jwt)
                .account(((CustomUserDetails) authentication.getPrincipal()).getAccount())
                .build();
    }


}
