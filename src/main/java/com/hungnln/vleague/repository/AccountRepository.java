package com.hungnln.vleague.repository;

import com.hungnln.vleague.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    Account findByEmail(String username);
//    Account findByEmail(String email);
//    Optional<Account> findById(UUID id);
}
