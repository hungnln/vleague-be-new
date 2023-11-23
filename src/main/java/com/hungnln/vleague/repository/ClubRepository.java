package com.hungnln.vleague.repository;


import com.hungnln.vleague.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    Page<Club> findAll(Pageable pageable);
    Optional<Club> findClubById(UUID id);
    Optional<Club> findClubByName(String name);
//    List<Club> findClubsByStadiumId(String name);

}
