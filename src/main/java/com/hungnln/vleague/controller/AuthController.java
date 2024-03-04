package com.hungnln.vleague.controller;

import com.hungnln.vleague.DTO.AuthenticateDTO;
import com.hungnln.vleague.constant.response.ResponseStatusDTO;
import com.hungnln.vleague.response.AccountLoginResponse;
import com.hungnln.vleague.response.AuthenticationResponse;
import com.hungnln.vleague.response.ResponseDTO;
import com.hungnln.vleague.service.AccountService;
import com.hungnln.vleague.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@Tag(name = "authenticate", description = "authenticate api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    private final AccountService accountService;
    private final AuthenticationService authenticationService;
    @PostMapping("/firebase")
    public ResponseEntity<ResponseDTO<AccountLoginResponse>> loginFirebase(@RequestBody AuthenticationResponse authenticationResponse) {
        ResponseDTO<AccountLoginResponse> responseDTO = new ResponseDTO<>();
        AccountLoginResponse accountLoginResponse = authenticationService.firebaseTokenCheckAccount(authenticationResponse.getToken());
        responseDTO.setData(accountLoginResponse);
        responseDTO.setMessage("Login success");
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);
    }
    @PostMapping("/admin")
    public ResponseEntity<ResponseDTO<AccountLoginResponse>> loginAdmin(@RequestBody AuthenticateDTO authenticateDTO) {
        ResponseDTO<AccountLoginResponse> responseDTO = new ResponseDTO<>();
        AccountLoginResponse accountLoginResponse = authenticationService.loginAdmin(authenticateDTO);
        responseDTO.setData(accountLoginResponse);
        responseDTO.setMessage("Login success");
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);
    }
}
