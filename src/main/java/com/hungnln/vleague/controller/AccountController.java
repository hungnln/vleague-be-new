package com.hungnln.vleague.controller;
import com.hungnln.vleague.DTO.ClubCreateDTO;
import com.hungnln.vleague.DTO.ClubUpdateDTO;
import com.hungnln.vleague.constant.account.AccountSuccessMessage;
import com.hungnln.vleague.constant.club.ClubSuccessMessage;
import com.hungnln.vleague.constant.response.ResponseStatusDTO;
import com.hungnln.vleague.repository.AccountRepository;
import com.hungnln.vleague.response.AccountResponse;
import com.hungnln.vleague.response.ClubResponse;
import com.hungnln.vleague.response.ResponseDTO;
import com.hungnln.vleague.response.ResponseWithTotalPage;
import com.hungnln.vleague.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "account", description = "account api")
@RequiredArgsConstructor
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("")
    @Operation(summary = "Get account list", description = "Get account list")
    ResponseEntity<ResponseDTO<ResponseWithTotalPage<AccountResponse>>> getAllAccounts(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "") String isBanned

    ) {
        ResponseDTO<ResponseWithTotalPage<AccountResponse>> responseDTO = new ResponseDTO<>();
        ResponseWithTotalPage<AccountResponse> list = accountService.getAllAccounts(pageIndex,pageSize,isBanned);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        responseDTO.setData(list);
        responseDTO.setMessage(AccountSuccessMessage.GET_ALL_SUCCESSFULL);
        return ResponseEntity.ok().body(responseDTO);
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseDTO<AccountResponse>> getAccountById(@PathVariable UUID id) {
        ResponseDTO<AccountResponse> responseDTO = new ResponseDTO<>();
        AccountResponse account = accountService.getAccountById(id);
        responseDTO.setData(account);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        responseDTO.setMessage(AccountSuccessMessage.GET_ACCOUNT_SUCCESSFULL);
        return ResponseEntity.ok().body(responseDTO);
    }

//    @PostMapping("")
//    ResponseEntity<ResponseDTO<ClubResponse>> addClub(@RequestBody @Valid ClubCreateDTO dto) throws BindException {
//        ResponseDTO<ClubResponse> responseDTO = new ResponseDTO<>();
//        ClubResponse club = clubService.addClub(dto);
//        responseDTO.setData(club);
//        responseDTO.setMessage(ClubSuccessMessage.CREATE_CLUB_SUCCESSFULL);
//        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
//        return ResponseEntity.ok().body(responseDTO);
//    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseDTO<AccountResponse>> updateAccountStatus(@PathVariable UUID id, @RequestParam boolean isBanned) {
        ResponseDTO<AccountResponse> responseDTO = new ResponseDTO<>();
        AccountResponse account = accountService.changeAccountStatus(id, isBanned);
        responseDTO.setData(account);
        responseDTO.setMessage(AccountSuccessMessage.UPDATE_ACCOUNT_SUCCESSFULL);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);

    }

//    @DeleteMapping("/{id}")
//    ResponseEntity<ResponseDTO<ClubResponse>> deleteClub(@PathVariable UUID id) {
//        ResponseDTO<ClubResponse> responseDTO = new ResponseDTO<>();
//        String msg = clubService.deleteClub(id);
//        responseDTO.setMessage(msg);
//        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
//        return ResponseEntity.ok().body(responseDTO);
//    }

}
