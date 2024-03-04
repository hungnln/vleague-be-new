package com.hungnln.vleague.service;

import com.google.firebase.auth.FirebaseToken;
import com.hungnln.vleague.constant.account.AccountFailMessage;
import com.hungnln.vleague.entity.Account;
import com.hungnln.vleague.entity.News;
import com.hungnln.vleague.exceptions.NotFoundException;
import com.hungnln.vleague.helper.AccountSpecification;
import com.hungnln.vleague.helper.SearchCriteria;
import com.hungnln.vleague.helper.SearchOperation;
import com.hungnln.vleague.repository.AccountRepository;
import com.hungnln.vleague.response.AccountResponse;
import com.hungnln.vleague.response.PaginationResponse;
import com.hungnln.vleague.response.ResponseWithTotalPage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public ResponseWithTotalPage<AccountResponse> getAllAccounts(int pageIndex, int pageSize,String isBanned){
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        List<Specification<Account>> specificationList = new ArrayList<>();
        if (isBanned.equals("true") || isBanned.equals("false")){
            AccountSpecification specification =  new AccountSpecification(new SearchCriteria("isBanned", SearchOperation.EQUALITY,isBanned));
            specificationList.add(specification);
        }
        Page<Account> pageResult = accountRepository.findAll(Specification.allOf(specificationList),pageable);
        ResponseWithTotalPage<AccountResponse> response = new ResponseWithTotalPage<>();
        List<AccountResponse> accountResponseList = new ArrayList<>();
        if (pageResult.hasContent()){
            for (Account account: pageResult.getContent()){
                AccountResponse accountResponse = modelMapper.map(account,AccountResponse.class);
                accountResponseList.add(accountResponse);
            }
        }
        response.setData(accountResponseList);
        PaginationResponse paginationResponse = PaginationResponse.builder()
                .pageIndex(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalCount((int) pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .build();
        response.setPagination(paginationResponse);
        return response;
    }
    public Account firebaseTokenCheckAccount(FirebaseToken decodeToken){
        if(decodeToken == null) throw new NotFoundException("Firebase token notfound");
        Account account = accountRepository.findByEmail(decodeToken.getEmail());
        if (account==null){
            account = Account.builder()
                    .email(decodeToken.getEmail())
                    .name(decodeToken.getName())
                    .imageURL(decodeToken.getPicture())
                    .isBanned(false)
                    .build();
            accountRepository.save(account);
        }
        return account;
    }
    public AccountResponse getAccountById(UUID id){
        Account account = accountRepository.findById(id).orElseThrow(()-> new NotFoundException(AccountFailMessage.ACCOUNT_NOT_FOUND));
        return modelMapper.map(account,AccountResponse.class);
    }
    public AccountResponse changeAccountStatus(UUID id,boolean isBanned){
        Account account = accountRepository.findById(id).orElseThrow(()-> new NotFoundException(AccountFailMessage.ACCOUNT_NOT_FOUND));
        account.setBanned(isBanned);
        accountRepository.save(account);
        return modelMapper.map(account,AccountResponse.class);
    }

}
